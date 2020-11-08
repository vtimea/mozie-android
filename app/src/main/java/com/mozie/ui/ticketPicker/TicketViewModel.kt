package com.mozie.ui.ticketPicker

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.cinemas.Screening
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.DateTime

class TicketViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    val networkError: LiveData<Event<String>> by this::mNetworkError
    val currentCinemas: LiveData<List<String>> by this::mCurrentCinemas
    val currentDates: LiveData<List<DateTime>> by this::mCurrentDates
    val currentTimes: LiveData<List<DateTime>> by this::mCurrentTimes
    val currentScreening: LiveData<Screening?> by this::mFilteredByTime

    private val resources: Resources = context.resources

    private val mNetworkError = MutableLiveData<Event<String>>()

    private val mCurrentCinemas = MutableLiveData<List<String>>()
    private val mCurrentDates = MutableLiveData<List<DateTime>>()
    private val mCurrentTimes = MutableLiveData<List<DateTime>>()

    private var mAllScreenings: List<Screening> = listOf()
    private var mFilteredByCinema: List<Screening> = listOf()
    private var mFilteredByDate: List<Screening> = listOf()
    private var mFilteredByTime = MutableLiveData<Screening?>()

    fun onCinemaSelected(cinemaName: String?) {
        if (cinemaName != null) {
            mFilteredByCinema = mAllScreenings.filter { it.cinemaName == cinemaName }.toList()
            val result = getDateFilters(mFilteredByCinema)
            mCurrentDates.value = result
        } else {
            mCurrentDates.value = listOf()
            mFilteredByDate = listOf()
            mCurrentTimes.value = listOf()
            mFilteredByTime.value = null
        }
    }

    fun onDateSelected(date: DateTime?) {
        if (date != null) {
            mFilteredByDate = mFilteredByCinema.filter {
                DateTime.parse(it.startTime).dayOfYear == date.dayOfYear
            }
            val result = getTimeFilters(mFilteredByDate).toList()
            mCurrentTimes.value = result
        } else {
            mCurrentTimes.value = listOf()
            mFilteredByTime.value = null
        }
    }

    fun onTimeSelected(date: DateTime?) {
        if (date != null) {
            mFilteredByTime.value =
                mFilteredByDate.findLast { DateTime.parse(it.startTime) == date }
        } else {
            mFilteredByTime.value = null
        }
    }

    fun getScreenings(movieId: String) {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        disposables.add(
            dataManager.networkHelper.getScreenings(
                token,
                movieId,
                object : Callback<List<Screening>>() {
                    override fun returnResult(t: List<Screening>) {
                        clearData()
                        mAllScreenings = t.toMutableList()
                        val result = getCinemaFilters(mAllScreenings)
                        mCurrentCinemas.value = result
                    }

                    override fun returnError(t: Throwable) {
                        clearData()
                        handleError()
                    }
                })
        )
    }

    private fun handleError() {
        mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
    }

    private fun getCinemaFilters(screenings: List<Screening>): List<String> {
        return screenings.groupBy { it.cinemaName!! }.keys.toList()
    }

    private fun getDateFilters(screenings: List<Screening>): List<DateTime> {
        val result: MutableSet<DateTime> = mutableSetOf()
        for (screening in screenings) {
            val date = DateTime.parse(screening.startTime).withMillisOfDay(0)
            result.add(date)
        }
        return result.toList()
    }

    private fun getTimeFilters(screenings: List<Screening>): List<DateTime> {
        val result: MutableSet<DateTime> = mutableSetOf()
        for (screening in screenings) {
            val date = DateTime.parse(screening.startTime)
            result.add(date)
        }
        return result.toList()
    }

    private fun clearData() {
        if (mAllScreenings.isNotEmpty()) {
            mFilteredByDate = listOf()
            mFilteredByCinema = listOf()
            mAllScreenings = listOf()

            mFilteredByTime.value = null
            mCurrentCinemas.value = listOf()
            mCurrentDates.value = listOf()
            mCurrentTimes.value = listOf()

        }
    }
}
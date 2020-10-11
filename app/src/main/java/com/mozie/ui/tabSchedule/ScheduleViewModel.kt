package com.mozie.ui.tabSchedule

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.cinemas.Screening
import com.mozie.data.network.model.movies.Cinema
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import com.mozie.ui.tabSchedule.models.ScheduleMovie
import com.mozie.ui.tabSchedule.models.ScheduleScreening
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.DateTime

class ScheduleViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) :
    BaseViewModel() {

    val cinemas: LiveData<List<Cinema>> by this::mCinemas
    val networkError: LiveData<Event<String>> by this::mNetworkError
    val screenings: LiveData<Map<ScheduleMovie, Map<String, List<ScheduleScreening>>>> by this::mScreenings

    private val resources: Resources = context.resources

    private val mCinemas = MutableLiveData<List<Cinema>>()
    private val mNetworkError = MutableLiveData<Event<String>>()
    private val mScreenings =
        MutableLiveData<Map<ScheduleMovie, Map<String, List<ScheduleScreening>>>>()

    fun getCinemas() {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        disposables.add(
            dataManager.networkHelper.getAllCinemas(
                token,
                object : Callback<List<Cinema>>() {
                    override fun returnResult(t: List<Cinema>) {
                        mCinemas.value = t
                    }

                    override fun returnError(t: Throwable) {
                        handleError()
                    }
                })
        )
    }

    fun getScreenings(cinema: Cinema, date: DateTime) {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        val dateParam = date.toString()
        dataManager.networkHelper.getScreenings(
            token,
            cinema.id ?: "",
            dateParam,
            object : Callback<List<Screening>>() {
                override fun returnResult(t: List<Screening>) {
                    processScreenings(t)
                }

                override fun returnError(t: Throwable) {
                    handleError()
                }
            })
    }

    private fun processScreenings(screenings: List<Screening>) {
        val result = HashMap<ScheduleMovie, MutableMap<String, MutableList<ScheduleScreening>>>()
        for (screening in screenings) {
            val type = "${screening.type} ${screening.voice}"
            val movie = ScheduleMovie(
                screening.movieId!!,
                screening.movieTitle,
                screening.movieGenre,
                screening.movieLength,
                screening.moviePoster
            )
            val scheduleScreening = ScheduleScreening(
                screening.id!!,
                DateTime(screening.startTime),
                screening.type,
                screening.voice
            )
            if (result.containsKey(movie)) {
                val movieMap: MutableMap<String, MutableList<ScheduleScreening>> =
                    result.getValue(movie)
                if (movieMap.containsKey(type)) {
                    movieMap[type]?.add(scheduleScreening)
                } else {
                    movieMap[type] = mutableListOf(scheduleScreening)
                }
                result[movie] = movieMap

            } else {
                val movieMap = HashMap<String, MutableList<ScheduleScreening>>()
                movieMap[type] = mutableListOf(scheduleScreening)
                result[movie] = movieMap
            }
        }
        mScreenings.value = result
    }

    private fun handleError() {
        mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
    }
}
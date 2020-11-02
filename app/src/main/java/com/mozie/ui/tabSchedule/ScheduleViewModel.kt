package com.mozie.ui.tabSchedule

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.cinemas.ScheduledScreening
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
    private val NUM_OF_TABS = 5

    val cinemas: LiveData<List<Cinema>> by this::mCinemas
    val networkError: LiveData<Event<String>> by this::mNetworkError
    val screenings: LiveData<Map<ScheduleMovie, Map<String, List<ScheduleScreening>>>> by this::mScreenings
    val tabs: LiveData<List<Pair<String, DateTime>>> by this::mTabs

    private val resources: Resources = context.resources

    private val mCinemas = MutableLiveData<List<Cinema>>()
    private val mNetworkError = MutableLiveData<Event<String>>()
    private val mScreenings =
        MutableLiveData<Map<ScheduleMovie, Map<String, List<ScheduleScreening>>>>()
    private val mTabs = MutableLiveData<List<Pair<String, DateTime>>>()

    private var mCurrentCinema: Cinema? = null

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

    fun onCinemaSelected(cinema: Cinema) {
        mCurrentCinema = cinema
        val tabs = mutableListOf<Pair<String, DateTime>>()
        tabs.add(Pair(resources.getString(R.string.day_today), DateTime.now()))
        for (i in 1 until NUM_OF_TABS) {
            val date = DateTime.now().plusDays(i)
            val day = getDayString(date)
            tabs.add(Pair(day, date))
        }
        mTabs.value = tabs
    }

    fun onTabSelected(index: Int) {
        mCurrentCinema?.let {
            mTabs.value?.get(index)?.second?.let { date ->
                getScreenings(
                    it,
                    date
                )
            }
        }
    }

    private fun getScreenings(cinema: Cinema, date: DateTime) {
        mCurrentCinema = cinema
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        val dateParam = date.toString()
        dataManager.networkHelper.getSchedule(
            token,
            cinema.id ?: "",
            dateParam,
            object : Callback<List<ScheduledScreening>>() {
                override fun returnResult(t: List<ScheduledScreening>) {
                    processScreenings(t)
                }

                override fun returnError(t: Throwable) {
                    handleError()
                }
            })
    }

    private fun processScreenings(scheduledScreenings: List<ScheduledScreening>) {
        val result = HashMap<ScheduleMovie, MutableMap<String, MutableList<ScheduleScreening>>>()
        for (screening in scheduledScreenings) {
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

    private fun getDayString(date: DateTime): String {
        return resources.getStringArray(R.array.days_of_week)[date.dayOfWeek - 1]
    }
}
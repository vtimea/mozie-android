package com.mozie.ui.tabSchedule

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.movies.Cinema
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class ScheduleViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) :
    BaseViewModel() {

    val cinemas: LiveData<List<Cinema>> by this::mCinemas
    val networkError: LiveData<Event<String>> by this::mNetworkError

    private val resources: Resources = context.resources

    private val mCinemas = MutableLiveData<List<Cinema>>()
    private val mNetworkError = MutableLiveData<Event<String>>()

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

    fun getScreenings(cinema: Cinema) {

    }

    private fun handleError() {
        mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
    }
}
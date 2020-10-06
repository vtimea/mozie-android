package com.mozie.ui.details

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.movies.MovieDetail
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class MovieDetailsViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    val detail: LiveData<MovieDetail> by this::mDetail
    val networkError: LiveData<Event<String>> by this::mNetworkError

    private val resources: Resources = context.resources

    private val mDetail = MutableLiveData<MovieDetail>()
    private val mNetworkError = MutableLiveData<Event<String>>()

    fun getDetails(movieId: String) {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        disposables.add(dataManager.networkHelper.getMovieDetails(token, movieId, object :
            Callback<MovieDetail>() {
            override fun returnResult(t: MovieDetail) {
                mDetail.value = t
            }

            override fun returnError(t: Throwable) {
                handleError()
            }
        }))
    }

    private fun handleError() {
        mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
    }
}
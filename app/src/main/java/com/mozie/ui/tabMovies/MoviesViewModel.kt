package com.mozie.ui.tabMovies

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.movies.FeaturedMovie
import com.mozie.data.network.model.movies.MoviesResponse
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class MoviesViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) :
    BaseViewModel() {

    val moviesRecommended: LiveData<List<FeaturedMovie>> by this::mMoviesRecommended
    val moviesNow: LiveData<List<FeaturedMovie>> by this::mMoviesNow
    val moviesSoon: LiveData<List<FeaturedMovie>> by this::mMoviesSoon
    val networkError: LiveData<Event<String>> by this::mNetworkError
    val navEvent: LiveData<Event<String>> by this::mNavEvent

    private val resources: Resources = context.resources

    private val mMoviesRecommended = MutableLiveData<List<FeaturedMovie>>()
    private val mMoviesNow = MutableLiveData<List<FeaturedMovie>>()
    private val mMoviesSoon = MutableLiveData<List<FeaturedMovie>>()
    private val mNetworkError = MutableLiveData<Event<String>>()
    private val mNavEvent = MutableLiveData<Event<String>>()

    fun getMovies() {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        disposables.add(
            dataManager.networkHelper.getAllMovies(
                token,
                object : Callback<MoviesResponse>() {
                    override fun returnResult(t: MoviesResponse) {
                        mMoviesRecommended.value = t.recommended ?: mutableListOf()
                        mMoviesNow.value = t.now ?: mutableListOf()
                        mMoviesSoon.value = t.soon ?: mutableListOf()
                    }

                    override fun returnError(t: Throwable) {
                        handleError()
                    }
                })
        )
    }

    fun onPosterClicked(movie: FeaturedMovie) {
        movie.id?.let {
            mNavEvent.value = Event(it)
        }
    }

    private fun handleError() {
        mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
    }
}
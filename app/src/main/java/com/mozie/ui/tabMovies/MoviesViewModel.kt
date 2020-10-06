package com.mozie.ui.tabMovies

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mozie.data.DataManager
import com.mozie.data.network.model.movies.Movie
import com.mozie.data.network.model.movies.MoviesResponse
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event

class MoviesViewModel @ViewModelInject constructor(private val dataManager: DataManager) :
    ViewModel() {

    val moviesRecommended: LiveData<List<Movie>> by this::mMoviesRecommended
    val moviesNow: LiveData<List<Movie>> by this::mMoviesNow
    val moviesSoon: LiveData<List<Movie>> by this::mMoviesSoon
    val networkError: LiveData<Event<String>> by this::mNetworkError

    private val mMoviesRecommended = MutableLiveData<List<Movie>>()
    private val mMoviesNow = MutableLiveData<List<Movie>>()
    private val mMoviesSoon = MutableLiveData<List<Movie>>()
    private val mNetworkError = MutableLiveData<Event<String>>()

    fun getMovies() {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        dataManager.networkHelper.getAllMovies(token, object : Callback<MoviesResponse>() {
            override fun returnResult(t: MoviesResponse) {
                mMoviesRecommended.value = t.recommended ?: mutableListOf()
                mMoviesNow.value = t.now ?: mutableListOf()
                mMoviesSoon.value = t.soon ?: mutableListOf()
            }

            override fun returnError(t: Throwable) {
                handleError()
            }
        })
    }

    private fun handleError() {
        mNetworkError.value = Event("Hálózati hiba.")
    }
}
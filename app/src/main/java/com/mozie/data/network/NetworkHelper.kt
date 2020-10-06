package com.mozie.data.network


import com.mozie.data.network.model.login.LoginResult
import com.mozie.data.network.model.movies.MovieDetail
import com.mozie.data.network.model.movies.MoviesResponse
import com.mozie.data.network.utils.Callback
import io.reactivex.disposables.Disposable

interface NetworkHelper {
    fun login(userId: String, accessToken: String, callback: Callback<LoginResult>): Disposable

    fun getAllMovies(accessToken: String, callback: Callback<MoviesResponse>): Disposable

    fun getMovieDetails(
        accessToken: String,
        movieId: String,
        callback: Callback<MovieDetail>
    ): Disposable
}
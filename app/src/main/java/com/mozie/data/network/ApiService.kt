package com.mozie.data.network

import com.mozie.data.network.model.login.LoginBody
import com.mozie.data.network.model.login.LoginResult
import com.mozie.data.network.model.movies.MoviesResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/auth/login")
    fun login(@Body body: LoginBody): Observable<LoginResult>

    @GET("/api/movies")
    fun getAllMovies(@Header("Authorization") token: String): Observable<MoviesResponse>
}
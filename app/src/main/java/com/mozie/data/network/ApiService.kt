package com.mozie.data.network

import com.mozie.data.network.model.cinemas.Screening
import com.mozie.data.network.model.login.LoginBody
import com.mozie.data.network.model.login.LoginResult
import com.mozie.data.network.model.movies.Cinema
import com.mozie.data.network.model.movies.MovieDetail
import com.mozie.data.network.model.movies.MoviesResponse
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService {
    @POST("/auth/login")
    fun login(@Body body: LoginBody): Observable<LoginResult>

    @GET("/api/movies")
    fun getAllMovies(@Header("Authorization") token: String): Observable<MoviesResponse>

    @GET("/api/movies/{id}")
    fun getMovieDetails(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Observable<MovieDetail>

    @GET("/api/cinemas")
    fun getAllCinemas(@Header("Authorization") token: String): Observable<List<Cinema>>

    @GET("/api/schedule")
    fun getScreenings(
        @Header("Authorization") token: String,
        @Query("date") date: String,
        @Query("cinema") cinemaId: String
    ): Observable<List<Screening>>
}
package com.mozie.data.network

import com.mozie.data.network.model.cinemas.ScheduledScreening
import com.mozie.data.network.model.cinemas.Screening
import com.mozie.data.network.model.cinemas.ScreeningRoom
import com.mozie.data.network.model.login.LoginBody
import com.mozie.data.network.model.login.LoginResult
import com.mozie.data.network.model.movies.Cinema
import com.mozie.data.network.model.movies.MovieDetail
import com.mozie.data.network.model.movies.MoviesResponse
import com.mozie.data.network.model.tickets.PaymentResult
import com.mozie.data.network.model.tickets.ResponseClientToken
import com.mozie.data.network.model.tickets.TicketOrder
import com.mozie.data.network.model.tickets.TicketType
import com.mozie.data.network.model.userTickets.UserTicket
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
    fun getSchedule(
        @Header("Authorization") token: String,
        @Query("date") date: String,
        @Query("cinema") cinemaId: String
    ): Observable<List<ScheduledScreening>>

    @GET("/api/screenings")
    fun getScreenings(
        @Header("Authorization") token: String,
        @Query("movieId") movieId: String
    ): Observable<List<Screening>>

    @GET("/api/tickets")
    fun getTicketTypes(
        @Header("Authorization") token: String,
        @Query("type") type: String?
    ): Observable<List<TicketType>>

    @GET("/api/screenings/room")
    fun getRoomForScreening(
        @Header("Authorization") token: String,
        @Query("screeningId") screeningId: String
    ): Observable<ScreeningRoom>

    @POST("/api/tickets/payment")
    fun getClientToken(
        @Header("Authorization") token: String,
        @Body ticketOrder: TicketOrder
    ): Observable<ResponseClientToken>

    @POST("/api/tickets/payment/nonce")
    fun sendNonce(
        @Header("Authorization") token: String,
        @Body paymentResult: PaymentResult
    ): Observable<Boolean>

    @GET("/api/user/tickets")
    fun getUserTickets(@Header("Authorization") token: String): Observable<Map<Int, UserTicket>>
}
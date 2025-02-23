package com.mozie.data.network

import android.content.Context
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
import com.mozie.data.network.utils.Callback
import com.mozie.data.network.utils.DefaultObserver
import com.mozie.data.network.utils.NetworkingUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityRetainedScoped
class NetworkHelperImpl @Inject constructor(@ApplicationContext private val context: Context) :
    NetworkHelper {
    private val apiService: ApiService = NetworkingUtils.getInstance(context)

    override fun login(
        userId: String,
        accessToken: String,
        callback: Callback<LoginResult>
    ): Disposable {
        val observer = DefaultObserver(callback)
        apiService.login(LoginBody(userId, accessToken))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun getAllMovies(accessToken: String, callback: Callback<MoviesResponse>): Disposable {
        val observer = DefaultObserver(callback)
        apiService.getAllMovies(accessToken)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun getMovieDetails(
        accessToken: String,
        movieId: String,
        callback: Callback<MovieDetail>
    ): Disposable {
        val observer = DefaultObserver(callback)
        apiService.getMovieDetails(accessToken, movieId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun getAllCinemas(accessToken: String, callback: Callback<List<Cinema>>): Disposable {
        val observer = DefaultObserver(callback)
        apiService.getAllCinemas(accessToken)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun getSchedule(
        accessToken: String,
        cinemaId: String,
        date: String,
        callback: Callback<List<ScheduledScreening>>
    ): Disposable {
        val observer = DefaultObserver(callback)
        apiService.getSchedule(accessToken, date, cinemaId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun getScreenings(
        accessToken: String,
        movieId: String,
        callback: Callback<List<Screening>>
    ): Disposable {
        val observer = DefaultObserver(callback)
        apiService.getScreenings(accessToken, movieId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun getTicketTypes(
        accessToken: String,
        type: String?,
        callback: Callback<List<TicketType>>
    ): Disposable {
        val observer = DefaultObserver(callback)
        apiService.getTicketTypes(accessToken, type)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun getRoomForScreening(
        accessToken: String,
        screeningId: String,
        callback: Callback<ScreeningRoom>
    ): Disposable {
        val observer = DefaultObserver(callback)
        apiService.getRoomForScreening(accessToken, screeningId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun getClientToken(
        accessToken: String,
        ticketOrder: TicketOrder,
        callback: Callback<ResponseClientToken>
    ): Disposable {
        val observer = DefaultObserver(callback)
        apiService.getClientToken(accessToken, ticketOrder)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun sendNonce(
        accessToken: String,
        paymentResult: PaymentResult,
        callback: Callback<Boolean>
    ): Disposable {
        val observer = DefaultObserver(callback)
        apiService.sendNonce(accessToken, paymentResult)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }

    override fun getUserTickers(
        accessToken: String,
        callback: Callback<Map<Int, UserTicket>>
    ): Disposable {
        val observer = DefaultObserver(callback)
        apiService.getUserTickets(accessToken)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
        return observer.disposable!!
    }
}
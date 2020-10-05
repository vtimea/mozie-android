package com.mozie.data.network

import com.mozie.data.network.model.LoginBody
import com.mozie.data.network.model.LoginResult
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/auth/login")
    fun login(@Body body: LoginBody): Observable<LoginResult>
}
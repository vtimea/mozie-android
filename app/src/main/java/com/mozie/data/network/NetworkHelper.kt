package com.mozie.data.network


import com.mozie.data.network.model.LoginResult
import com.mozie.data.network.utils.Callback
import io.reactivex.disposables.Disposable

interface NetworkHelper {
    fun login(userId: String, accessToken: String, callback: Callback<LoginResult>): Disposable
}
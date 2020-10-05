package com.mozie.data.network

import com.mozie.data.network.model.LoginBody
import com.mozie.data.network.model.LoginResult
import com.mozie.data.network.utils.Callback
import com.mozie.data.network.utils.DefaultObserver
import com.mozie.data.network.utils.NetworkingUtils
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityRetainedScoped
class NetworkHelperImpl @Inject constructor() : NetworkHelper {
    private val apiService: ApiService = NetworkingUtils.getInstance()

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
}
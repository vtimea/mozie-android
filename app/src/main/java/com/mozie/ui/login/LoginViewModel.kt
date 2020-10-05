package com.mozie.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mozie.data.network.NetworkHelper
import com.mozie.data.network.model.LoginResult
import com.mozie.data.network.utils.Callback
import com.mozie.data.prefs.PrefsHelper

class LoginViewModel @ViewModelInject constructor(
    private val prefsHelper: PrefsHelper,
    private val networkHelper: NetworkHelper
) :
    ViewModel() {
    private var mNavigator: LoginNavigator? = null

    fun login(userId: String?, accessToken: String?) {
        if (userId == null || accessToken == null) {
            getNavigator()?.handleError("")
            return
        }
        networkHelper.login(userId, accessToken, object : Callback<LoginResult>() {
            override fun returnResult(t: LoginResult) {
                val token: String? = t.token
                if (token == null) {
                    getNavigator()?.handleError("")
                    return
                }
                prefsHelper.saveAccessToken(token)
                getNavigator()?.loginSuccess()
            }

            override fun returnError(t: Throwable) {
                getNavigator()?.handleError("")
            }
        })
    }

    fun getNavigator(): LoginNavigator? {
        return mNavigator
    }

    fun setNavigator(navigator: LoginNavigator) {
        mNavigator = navigator
    }
}
package com.mozie.ui.login

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.login.LoginResult
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class LoginViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) :
    BaseViewModel() {

    val loginError: LiveData<Event<String>> by this::mLoginError
    val loginSuccess: LiveData<Event<String>> by this::mLoginSuccess

    private val mLoginError = MutableLiveData<Event<String>>()
    private val mLoginSuccess = MutableLiveData<Event<String>>()

    private val resources: Resources = context.resources

    fun login(userId: String?, accessToken: String?) {
        if (userId == null || accessToken == null) {
            onError(ErrorTypes.FacebookError)
            return
        }
        disposables.add(
            dataManager.networkHelper.login(
                userId,
                accessToken,
                object : Callback<LoginResult>() {
                    override fun returnResult(t: LoginResult) {
                        val token: String? = t.token
                        val expiration: String? = t.expiration
                        if (token == null || expiration == null) {
                            onError(ErrorTypes.Unknown)
                            return
                        }
                        dataManager.prefsHelper.saveAccessToken(token, expiration, userId)
                        onLoginSuccess()
                    }

                    override fun returnError(t: Throwable) {
                        onError(ErrorTypes.NetworkError)
                    }
                })
        )
    }

    fun logout() {
        dataManager.prefsHelper.logout()
    }

    fun onFacebookError() {
        onError(ErrorTypes.FacebookError)
    }

    private fun onError(errorType: ErrorTypes) {
        val msg: String =
            when (errorType) {
                ErrorTypes.FacebookError -> resources.getString(R.string.error_login_facebook)
                ErrorTypes.NetworkError -> resources.getString(R.string.error_login_network)
                ErrorTypes.Unknown -> resources.getString(R.string.error_login_unknown)
            }
        mLoginError.value = Event(msg)
    }

    private fun onLoginSuccess() {
        mLoginSuccess.value = Event(resources.getString(R.string.login_success))
    }

    private enum class ErrorTypes {
        FacebookError,
        NetworkError,
        Unknown
    }
}
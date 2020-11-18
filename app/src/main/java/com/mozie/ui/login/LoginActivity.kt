package com.mozie.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.mozie.R
import com.mozie.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_LOGOUT = "extra_logout"
    }

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        updateLoginStatus()
        initObservers()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    viewModel.login(
                        loginResult?.accessToken?.userId,
                        loginResult?.accessToken?.token
                    )
                }

                override fun onCancel() {
                    // do nothing
                }

                override fun onError(exception: FacebookException) {
                    viewModel.onFacebookError()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateLoginStatus() {
        if (intent.hasExtra(EXTRA_LOGOUT)) {
            handleLoginError(getString(R.string.msg_session_expired))
        }
    }

    private fun initObservers() {
        viewModel.loginError.observe(this, { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                handleLoginError(it)
            }
        })

        viewModel.loginSuccess.observe(this, { event ->
            event.getContentIfNotHandledOrReturnNull()?.let {
                handleLoginSuccess(it)
            }
        })
    }

    private fun handleLoginSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun handleLoginError(message: String) {
        viewModel.logout()
        LoginManager.getInstance().logOut()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        finishAffinity()
        finish()
    }
}
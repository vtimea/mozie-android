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
import com.mozie.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), LoginNavigator {
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewModel.setNavigator(this) // todo inject

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
                    // todo
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun loginSuccess() {
        Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun handleError(message: String) {
        LoginManager.getInstance().logOut()
        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show()
    }
}
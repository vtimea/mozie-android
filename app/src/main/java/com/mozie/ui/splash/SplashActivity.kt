package com.mozie.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.mozie.R
import com.mozie.ui.home.HomeActivity
import com.mozie.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initObservers()
        viewModel.onApplicationStarted()
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                when (it) {
                    SplashViewModel.Destinations.Login -> navigateToLogin()
                    SplashViewModel.Destinations.Home -> navigateToHome()
                }
            }
        })
    }

    private fun navigateToLogin() {
        LoginManager.getInstance().logOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
package com.mozie.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mozie.data.DataManager
import com.mozie.ui.Event
import org.joda.time.DateTime

class SplashViewModel @ViewModelInject constructor(
    private val dataManager: DataManager
) : ViewModel() {

    val navigationEvent = MutableLiveData<Event<Destinations>>()

    fun onApplicationStarted() {
        val accessToken = dataManager.prefsHelper.getAccessToken()
        val expiration = dataManager.prefsHelper.getAccessTokenExpiration()
        if (accessToken == null || expiration == null || DateTime(expiration).isBeforeNow) {
            dataManager.prefsHelper.clearAccessToken()
            navigationEvent.value = Event(Destinations.Login)
        } else {
            navigationEvent.value = Event(Destinations.Home)
        }
    }

    enum class Destinations {
        Login,
        Home
    }
}
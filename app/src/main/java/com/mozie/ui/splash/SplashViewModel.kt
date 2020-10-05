package com.mozie.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mozie.data.DataManager
import com.mozie.ui.Event

class SplashViewModel @ViewModelInject constructor(
    private val dataManager: DataManager
) : ViewModel() {

    val navigationEvent = MutableLiveData<Event<Destinations>>()

    fun onApplicationStarted() {
        val accessToken = dataManager.prefsHelper.getAccessToken()
        // TODO handle expiration
        if (accessToken == null) {
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
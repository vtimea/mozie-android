package com.mozie.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.data.DataManager
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import org.joda.time.DateTime

class SplashViewModel @ViewModelInject constructor(
    private val dataManager: DataManager
) : BaseViewModel() {

    val navigationEvent: LiveData<Event<Destinations>> by this::mNavigationEvent

    private val mNavigationEvent = MutableLiveData<Event<Destinations>>()

    fun onApplicationStarted() {
        val accessToken = dataManager.prefsHelper.getAccessToken()
        val expiration = dataManager.prefsHelper.getAccessTokenExpiration()
        if (accessToken == null || expiration == null || DateTime(expiration).isBeforeNow) {
            dataManager.prefsHelper.logout()
            mNavigationEvent.value = Event(Destinations.Login)
        } else {
            mNavigationEvent.value = Event(Destinations.Home)
        }
    }

    enum class Destinations {
        Login,
        Home
    }
}
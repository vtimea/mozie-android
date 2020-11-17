package com.mozie.ui.tabTickets

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.userTickets.UserTicket
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class TicketsViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) :
    BaseViewModel() {
    val networkError: LiveData<Event<String>> by this::mNetworkError
    val tickets: LiveData<Map<Int, UserTicket>> by this::mTickets

    private val resources: Resources = context.resources

    private val mNetworkError = MutableLiveData<Event<String>>()
    private val mTickets = MutableLiveData<Map<Int, UserTicket>>()

    fun getTickets() {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        disposables.add(dataManager.networkHelper.getUserTickers(token, object :
            Callback<Map<Int, UserTicket>>() {
            override fun returnResult(t: Map<Int, UserTicket>) {
                mTickets.value = t
            }

            override fun returnError(t: Throwable) {
                handleError()
            }

        }))
    }

    private fun handleError(message: String? = null) {
        if (message == null) {
            mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
        } else {
            mNetworkError.value = Event(message)
        }
    }
}
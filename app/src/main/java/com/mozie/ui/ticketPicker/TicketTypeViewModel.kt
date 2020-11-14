package com.mozie.ui.ticketPicker

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.tickets.TicketType
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class TicketTypeViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    val networkError: LiveData<Event<String>> by this::mNetworkError
    val ticketTypes: LiveData<List<TicketType>> by this::mCurrentTicketTypes
    val userSelectedTickets = MutableLiveData<Map<String, Int>>()

    private val resources: Resources = context.resources

    private val mNetworkError = MutableLiveData<Event<String>>()

    private var mAllTicketTypes: List<TicketType> = listOf()
    private val mCurrentTicketTypes = MutableLiveData<List<TicketType>>()

    fun onTicketTypeChanged(type: String?) {
        if (type == null) {
            mCurrentTicketTypes.value = listOf()
            return
        }
        mCurrentTicketTypes.value = filterTicketTypes(type)
    }

    fun getChosenTicketCount(): Int {
        var count = 0
        for (ticket in userSelectedTickets.value ?: mutableMapOf()) {
            count += ticket.value
        }
        return count
    }

    fun getTicketTypes() {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        disposables.add(
            dataManager.networkHelper.getTicketTypes(
                token,
                "",
                object : Callback<List<TicketType>>() {
                    override fun returnResult(t: List<TicketType>) {
                        mAllTicketTypes = t
                    }

                    override fun returnError(t: Throwable) {
                        handleError()
                    }
                })
        )
    }

    private fun handleError() {
        mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
    }

    private fun filterTicketTypes(type: String): List<TicketType> {
        return mAllTicketTypes.filter {
            it.type == type
        }
    }
}
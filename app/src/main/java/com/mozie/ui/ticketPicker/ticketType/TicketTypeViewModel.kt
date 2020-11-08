package com.mozie.ui.ticketPicker.ticketType

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

    private val resources: Resources = context.resources

    private val mNetworkError = MutableLiveData<Event<String>>()

    private val mAllTicketTypes = MutableLiveData<List<TicketType>>()
    private val mCurrentTicketTypes = MutableLiveData<List<TicketType>>()

    fun getTicketTypes() {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        disposables.add(
            dataManager.networkHelper.getTicketTypes(
                token,
                "",
                object : Callback<List<TicketType>>() {
                    override fun returnResult(t: List<TicketType>) {
                        mAllTicketTypes.value = t
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
        val tickets = listOf<TicketType>()
        mAllTicketTypes.value?.filter {
            it.type == type
        }
        return tickets
    }
}
package com.mozie.ui.ticketPicker

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.braintreepayments.api.models.PaymentMethodNonce
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.tickets.PaymentResult
import com.mozie.data.network.model.tickets.ResponseClientToken
import com.mozie.data.network.model.tickets.TicketOrder
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class TicketPaymentViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    val networkError: LiveData<Event<String>> by this::mNetworkError
    val showDropInUI: LiveData<ResponseClientToken> by this::mShowDropInUI

    private val resources: Resources = context.resources

    private val mNetworkError = MutableLiveData<Event<String>>()
    private val mShowDropInUI = MutableLiveData<ResponseClientToken>()

    fun startPayment(ticketOrder: TicketOrder) {
        val userId = dataManager.prefsHelper.getUserId() ?: ""
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        ticketOrder.userId = userId
        disposables.add(dataManager.networkHelper.getClientToken(token, ticketOrder, object :
            Callback<ResponseClientToken>() {
            override fun returnResult(t: ResponseClientToken) {
                mShowDropInUI.value = t
            }

            override fun returnError(t: Throwable) {
                handleError()
            }
        }))
    }

    fun sendNonce(nonce: PaymentMethodNonce) {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        val transactionId = mShowDropInUI.value?.transactionId ?: 0
        val paymentResult = PaymentResult()
        paymentResult.nonce = nonce.nonce
        paymentResult.transactionId = transactionId
        disposables.add(dataManager.networkHelper.sendNonce(token, paymentResult, object :
            Callback<Boolean>() {
            override fun returnResult(t: Boolean) {
                Log.i("dl0csh", "PAYMENT SUCCESS = $t")
            }

            override fun returnError(t: Throwable) {
                handleError()
            }
        }))
    }

    private fun handleError() {
        mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
    }
}
package com.mozie.ui.ticketPicker

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.braintreepayments.api.dropin.DropInResult
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
    val showDropInUI: LiveData<Event<ResponseClientToken>> by this::mShowDropInUI
    val loading: LiveData<Event<Boolean>> by this::mLoading
    val paymentResultEvent: LiveData<Event<Boolean>> by this::mPaymentResultEvent

    private val resources: Resources = context.resources

    private val mNetworkError = MutableLiveData<Event<String>>()
    private val mShowDropInUI = MutableLiveData<Event<ResponseClientToken>>()
    private val mLoading = MutableLiveData<Event<Boolean>>()
    private val mPaymentResultEvent = MutableLiveData<Event<Boolean>>()

    private var mClientToken: ResponseClientToken? = null
    private var mTicketOrder: TicketOrder? = null

    fun startPayment(ticketOrder: TicketOrder) {
        mLoading.value = Event(true)
        val userId = dataManager.prefsHelper.getUserId() ?: ""
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        ticketOrder.userId = userId
        if (mClientToken != null && mTicketOrder == ticketOrder) {
            mShowDropInUI.value = Event(mClientToken!!)
            return
        }
        mTicketOrder = ticketOrder
        disposables.add(dataManager.networkHelper.getClientToken(token, ticketOrder, object :
            Callback<ResponseClientToken>() {
            override fun returnResult(t: ResponseClientToken) {
                mClientToken = t
                mShowDropInUI.value = Event(t)
            }

            override fun returnError(t: Throwable) {
                onPaymentFinished()
                handleError()
            }
        }))
    }

    fun onDropInResultOK(result: DropInResult) {
        val nonce = result.paymentMethodNonce
        if (nonce == null) {
            handleError(resources.getString(R.string.error_braintree))
            return
        }
        sendNonce(nonce)
    }

    fun onDropInResultCanceled() {
        onPaymentCanceled()
    }

    fun onDropInResultError(exception: Exception) {
        onPaymentFinished()
        handleError(exception.localizedMessage)
    }

    private fun sendNonce(nonce: PaymentMethodNonce) {
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        val transactionId = mShowDropInUI.value?.peekContent()?.transactionId ?: 0
        val paymentResult = PaymentResult()
        paymentResult.nonce = nonce.nonce
        paymentResult.transactionId = transactionId
        disposables.add(dataManager.networkHelper.sendNonce(token, paymentResult, object :
            Callback<Boolean>() {
            override fun returnResult(t: Boolean) {
                onPaymentFinished()
                Log.i("dl0csh", "PAYMENT SUCCESS = $t")
            }

            override fun returnError(t: Throwable) {
                onPaymentFinished()
                handleError()
            }
        }))
    }

    private fun onPaymentCanceled() {
        mLoading.value = Event(false)
    }

    private fun onPaymentFinished() {
        mClientToken = null
        mTicketOrder = null
        mLoading.value = Event(false)
    }

    private fun handleError(message: String? = null) {
        if (message == null) {
            mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
        } else {
            mNetworkError.value = Event(message)
        }
    }
}
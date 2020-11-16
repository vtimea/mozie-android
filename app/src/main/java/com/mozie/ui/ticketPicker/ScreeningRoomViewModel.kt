package com.mozie.ui.ticketPicker

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mozie.R
import com.mozie.data.DataManager
import com.mozie.data.network.model.cinemas.ScreeningRoom
import com.mozie.data.network.model.cinemas.Seat
import com.mozie.data.network.utils.Callback
import com.mozie.ui.Event
import com.mozie.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class ScreeningRoomViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    val networkError: LiveData<Event<String>> by this::mNetworkError
    val seats: LiveData<ScreeningRoom> by this::mSeats
    val selectedSeats: LiveData<MutableList<Seat>> by this::mSelectedSeats

    private val resources: Resources = context.resources

    private val mNetworkError = MutableLiveData<Event<String>>()
    private val mSeats = MutableLiveData<ScreeningRoom>()
    private val mSelectedSeats = MutableLiveData<MutableList<Seat>>(mutableListOf())
    private var mSelectionLimit: Int = 0

    fun onTicketCountChange(count: Int) {
        mSelectionLimit = count
    }

    fun onSeatSelected(seat: Seat): Boolean {
        if (mSelectedSeats.value!!.size + 1 <= mSelectionLimit) {
            val newList = mSelectedSeats.value ?: mutableListOf()
            newList.add(seat)
            mSelectedSeats.value = newList
            return true
        }
        return false
    }

    fun onSeatDeselected(seat: Seat) {
        val newList = mSelectedSeats.value ?: mutableListOf()
        newList.remove(seat)
        mSelectedSeats.value = newList
    }

    fun getSelectedSeatIds(): List<Int> {
        val seatIds = mutableListOf<Int>()
        for (seat in mSelectedSeats.value ?: listOf()) {
            seatIds.add(seat.id ?: continue)
        }
        return seatIds
    }

    fun getRoomForScreening(screeningId: String?) {
        mSelectedSeats.value = mutableListOf()
        if (screeningId == null) {
            mSeats.value = null
            return
        }
        val token = dataManager.prefsHelper.getAccessToken() ?: ""
        disposables.add(
            dataManager.networkHelper.getRoomForScreening(
                token,
                screeningId,
                object : Callback<ScreeningRoom>() {
                    override fun returnResult(t: ScreeningRoom) {
                        mSeats.value = t
                    }

                    override fun returnError(t: Throwable) {
                        handleError()
                    }
                })
        )
    }

    fun getSelectedSeatCount() = mSelectedSeats.value!!.size

    fun getSeatCountLimit() = mSelectionLimit

    private fun handleError() {
        mNetworkError.value = Event(resources.getString(R.string.error_network_problem))
    }
}
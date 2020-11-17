package com.mozie.ui.ticketPicker.seatpicker

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mozie.R
import com.mozie.data.network.model.cinemas.Seat
import com.mozie.databinding.FragmentSeatPickerBinding
import com.mozie.ui.ticketPicker.ScreeningRoomViewModel
import com.mozie.ui.ticketPicker.TicketFilterViewModel
import com.mozie.ui.ticketPicker.TicketTypeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeatPickerFragment : Fragment() {
    companion object {
        fun newInstance() = SeatPickerFragment()
    }

    private lateinit var binding: FragmentSeatPickerBinding
    private lateinit var viewModelTicketType: TicketTypeViewModel
    private lateinit var viewModelTicketFilter: TicketFilterViewModel
    private lateinit var viewModelRoom: ScreeningRoomViewModel

    fun setSeatPickerVisibility(visible: Boolean) {
        if (visible) {
            binding.seatsZoomLayout.visibility = View.VISIBLE
        } else {
            binding.seatsZoomLayout.visibility = View.INVISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeatPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModelTicketType =
            ViewModelProvider(requireActivity()).get(TicketTypeViewModel::class.java)
        viewModelTicketFilter =
            ViewModelProvider(requireActivity()).get(TicketFilterViewModel::class.java)
        viewModelRoom =
            ViewModelProvider(requireActivity()).get(ScreeningRoomViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        binding.seatsZoomLayout.setZoomEnabled(true)
        viewModelRoom.getRoomForScreening(viewModelTicketFilter.currentScreening.value?.id)
    }

    override fun onPause() {
        binding.seatsZoomLayout.setZoomEnabled(false)
        super.onPause()
    }

    private fun initViews() {}

    private fun initObservers() {
        viewModelRoom.seats.observe(viewLifecycleOwner, {
            if (it?.seats == null || it.seats.isNullOrEmpty()) {
                clearSelectedSeats()
                showEmptyView(true)
            } else {
                initSeatView(it.numRows!!, it.numCols!!, it.seats)
                showEmptyView(false)
            }
        })
    }

    private fun initSeatView(numRow: Int, numCol: Int, seats: List<Seat>) {
        if (seats.isEmpty())
            return

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)

        binding.gridSeats.removeAllViews()
        binding.gridSeats.columnCount = numCol
        binding.gridSeats.rowCount = numRow
        for (i in 1..numCol) {
            for (j in 1..numRow) {
                val seat: Seat = seats.find { it.col == i && it.row == j }!!
                val checkBox = AppCompatCheckBox(requireContext())
                when (seat.available) {
                    false -> checkBox.isEnabled = false
                    true -> checkBox.setOnClickListener {
                        if (checkBox.isChecked) {
                            onSeatSelected(checkBox, seat)
                        } else {
                            onSeatDeselected(seat)
                        }
                        binding.gridSeats.invalidate()
                    }
                }
                val params =
                    LinearLayout.LayoutParams(binding.gridSeats.layoutParams as LinearLayout.LayoutParams)
                params.width = displayMetrics.widthPixels / numCol
                params.height = params.width
                checkBox.layoutParams = params
                checkBox.setButtonDrawable(R.drawable.seats_selector)
                binding.gridSeats.addView(checkBox)
            }
        }

        val params =
            LinearLayout.LayoutParams(binding.canvas.layoutParams as LinearLayout.LayoutParams)
        params.width = displayMetrics.widthPixels
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        binding.canvas.layoutParams = params
        binding.seatsZoomLayout.zoomTo(0.9f, true)
    }

    private fun onSeatSelected(checkBox: AppCompatCheckBox, seat: Seat) {
        val successful = viewModelRoom.onSeatSelected(seat)
        if (!successful) {
            checkBox.isChecked = false
        }
    }

    private fun onSeatDeselected(seat: Seat) {
        viewModelRoom.onSeatDeselected(seat)
    }

    private fun clearSelectedSeats() {
        for (child in binding.gridSeats.children) {
            val checkbox = (child as AppCompatCheckBox)
            checkbox.isChecked = false
        }
    }

    private fun showEmptyView(show: Boolean) {
        if (show) {
            binding.seatsZoomLayout.visibility = View.GONE
            binding.tvEmptyText.visibility = View.VISIBLE
        } else {
            binding.tvEmptyText.visibility = View.GONE
            binding.seatsZoomLayout.visibility = View.VISIBLE
        }
    }
}
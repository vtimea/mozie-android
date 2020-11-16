package com.mozie.ui.ticketPicker.summary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mozie.R
import com.mozie.data.network.model.cinemas.Screening
import com.mozie.data.network.model.tickets.TicketType
import com.mozie.databinding.FragmentSummaryBinding
import com.mozie.ui.ticketPicker.TicketFilterViewModel
import com.mozie.ui.ticketPicker.TicketTypeViewModel
import org.joda.time.DateTime

class SummaryFragment : Fragment() {
    companion object {
        fun newInstance() = SummaryFragment()
    }

    private lateinit var binding: FragmentSummaryBinding
    private lateinit var viewModelTicketType: TicketTypeViewModel
    private lateinit var viewModelTicketFilter: TicketFilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
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
    }

    private fun initViews() {
        // do nothing?
    }

    private fun initObservers() {
        viewModelTicketFilter.currentScreening.observe(viewLifecycleOwner, {
            setHeaderCard(it)
        })
        viewModelTicketType.userSelectedTickets.observe(viewLifecycleOwner, {
            onTicketChange(it, viewModelTicketType.ticketTypes.value ?: listOf())
        })
        viewModelTicketType.ticketTypes.observe(viewLifecycleOwner, {
            onTicketChange(viewModelTicketType.userSelectedTickets.value ?: mapOf(), it)
        })
    }

    private fun setHeaderCard(screening: Screening?) {
        if (screening != null) {
            binding.tvMovieTitle.text = getString(
                R.string.summary_movie_name_header,
                screening.movieTitle,
                screening.cinemaName
            )
            val date = DateTime.parse(screening.startTime)
            binding.tvScreeningDate.text = getString(
                R.string.summary_screening_date_format,
                date.year, date.monthOfYear, date.dayOfMonth, date.hourOfDay, date.minuteOfHour
            )
        } else {
            binding.tvMovieTitle.text = getString(R.string.summary_empty_header)
            binding.tvScreeningDate.text = getString(R.string.summary_empty_header)
        }
    }

    private fun setSelectedTickets(tickets: Map<TicketType, Int>) {
        binding.summaryTickets.removeAllViews()
        for (ticketType in tickets.keys) {
            val quantity = tickets[ticketType]
            if (quantity == null || quantity == 0) {
                continue
            }
            val price = ticketType.price ?: 0
            val view = layoutInflater.inflate(R.layout.view_ticket_summary_row, null)
            view.findViewById<TextView>(R.id.tvTicketName).text = ticketType.name   //TODO
            view.findViewById<TextView>(R.id.tvTicketPrice).text =
                getString(R.string.price_huf, price)
            view.findViewById<TextView>(R.id.tvTicketQuantity).text =
                getString(R.string.quantity, quantity)
            binding.summaryTickets.addView(view)
        }
        binding.tvSumPrice.text = getString(R.string.price_huf, viewModelTicketType.getSumPrice())
    }

    private fun onTicketChange(
        selectedTickets: Map<String, Int>, ticketTypes: List<TicketType>
    ) {
        val result: MutableMap<TicketType, Int> = mutableMapOf()
        for (ticket in selectedTickets) {
            val type = ticketTypes.findLast { it.name == ticket.key } ?: continue
            result[type] = ticket.value
        }
        setSelectedTickets(result)
    }
}
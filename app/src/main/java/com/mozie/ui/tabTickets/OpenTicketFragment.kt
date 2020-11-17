package com.mozie.ui.tabTickets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mozie.data.network.model.userTickets.UserTicket
import com.mozie.databinding.FragmentOpenTicketBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class OpenTicketFragment : Fragment() {
    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: FragmentOpenTicketBinding

    companion object {
        const val KEY_SCREENING_ID = "key_screening"
        fun newInstance() = OpenTicketFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(TicketsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpenTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val key = arguments?.getInt(KEY_SCREENING_ID) ?: 0
        val ticket = viewModel.tickets.value?.get(key)
        ticket?.let { initViews(it) }
    }

    private fun initViews(ticket: UserTicket) {
        binding.btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }
        binding.ticketPager.adapter = AdapterOpenTickets(ticket, this)
        binding.ticketPager.setPadding(65, 5, 65, 5)
        binding.ticketPager.id = Random.nextInt(50)
        binding.dotsIndicator.setViewPager(binding.ticketPager)
    }
}
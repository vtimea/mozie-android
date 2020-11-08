package com.mozie.ui.ticketPicker.ticketType

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mozie.databinding.FragmentTicketTypesBinding
import com.mozie.ui.Event
import com.mozie.ui.ticketPicker.TicketPickerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TicketTypeFragment : Fragment() {
    companion object {
        fun newInstance() = TicketTypeFragment()
    }

    private lateinit var binding: FragmentTicketTypesBinding
    private val viewModel: TicketTypeViewModel by viewModels()
    private var listAdapter: RvAdapterTicketTypes = RvAdapterTicketTypes(listOf())
    private val mTicketTypeEvent: RvAdapterTicketTypes.OnTicketTypeEvent =
        object : RvAdapterTicketTypes.OnTicketTypeEvent() {
            override fun onTicketTypesChanged(count: Int) {
                (requireActivity() as TicketPickerActivity).onChosenTicketCountChanged(count)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTicketTypesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
        viewModel.getTicketTypes()
    }

    fun onTicketTypeChanged(type: String?) {
        viewModel.onTicketTypeChanged(type)
    }

    fun getChosenTicketCount(): Int = listAdapter.getChosenTicketsSize()

    private fun initViews() {
        binding.rvTicketTypes.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listAdapter.setTicketTypeListener(mTicketTypeEvent)
        binding.rvTicketTypes.adapter = listAdapter
    }

    private fun initObservers() {
        viewModel.networkError.observe(viewLifecycleOwner, {
            handleError(it)
        })
        viewModel.ticketTypes.observe(viewLifecycleOwner, {
            showEmptyView(it.isNullOrEmpty())
            if (!it.isNullOrEmpty()) {
                listAdapter.setTicketTypes(it)
            }
        })
    }

    private fun handleError(event: Event<String>) {
        Toast.makeText(
            requireContext(),
            event.getContentIfNotHandledOrReturnNull(),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showEmptyView(show: Boolean) {
        if (show) {
            binding.rvTicketTypes.visibility = View.GONE
            binding.tvEmptyText.visibility = View.VISIBLE
        } else {
            binding.tvEmptyText.visibility = View.GONE
            binding.rvTicketTypes.visibility = View.VISIBLE
        }
    }
}
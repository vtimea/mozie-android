package com.mozie.ui.tabTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mozie.databinding.FragmentTicketsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TicketsFragment : Fragment() {
    private val viewModel: TicketsViewModel by viewModels()
    private lateinit var binding: FragmentTicketsBinding

    companion object {
        fun newInstance() = TicketsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.rvTickets.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initObservers() {
        viewModel.tickets.observe(viewLifecycleOwner, {
            binding.rvTickets.adapter = AdapterTicketsList(it)
            showLoading(false)
            //todo empty view
        })
    }

    override fun onResume() {
        super.onResume()
        showLoading(true)
        viewModel.getTickets()
    }

    private fun showLoading(loading: Boolean) {
        if (loading) {
            binding.rvTickets.visibility = View.GONE
            binding.loadingLayout.startShimmerAnimation()
            binding.loadingLayout.visibility = View.VISIBLE
        } else {
            binding.loadingLayout.stopShimmerAnimation()
            binding.loadingLayout.visibility = View.GONE
            binding.rvTickets.visibility = View.VISIBLE

        }
    }
}
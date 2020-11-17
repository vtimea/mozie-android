package com.mozie.ui.tabTickets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mozie.R
import com.mozie.databinding.FragmentTicketsBinding
import com.mozie.ui.tabTickets.OpenTicketFragment.Companion.KEY_SCREENING_ID
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TicketsFragment : Fragment(), ItemSelectionListener {
    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: FragmentTicketsBinding
    private val mOpenTicketFragment: OpenTicketFragment = OpenTicketFragment.newInstance()

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(TicketsViewModel::class.java)
    }

    private fun initViews() {
        binding.rvTickets.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initObservers() {
        viewModel.tickets.observe(viewLifecycleOwner, {
            showLoading(false)
            if (it.isEmpty()) {
                binding.rvTickets.visibility = View.GONE
                binding.emptyLayout.visibility = View.VISIBLE
            } else {
                binding.rvTickets.visibility = View.VISIBLE
                binding.emptyLayout.visibility = View.GONE
                val adapter = AdapterTicketsList(it)
                adapter.itemSelectionListener = this
                binding.rvTickets.adapter = adapter
            }
        })
    }

    override fun onResume() {
        closeTicketIfOpen()
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

    private fun closeTicketIfOpen() {
        requireActivity().supportFragmentManager.beginTransaction()
            .remove(mOpenTicketFragment)
            .commit()
    }

    override fun onItemSelected(itemId: Int) {
        val bundle = Bundle()
        bundle.putInt(KEY_SCREENING_ID, itemId)
        mOpenTicketFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.root, mOpenTicketFragment)
            .addToBackStack(null)
            .commit()
    }
}
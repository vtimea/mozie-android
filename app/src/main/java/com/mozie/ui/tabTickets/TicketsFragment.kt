package com.mozie.ui.tabTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mozie.databinding.FragmentTicketsBinding

class TicketsFragment : Fragment() {
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
}
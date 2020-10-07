package com.mozie.ui.tabSchedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mozie.R
import com.mozie.data.network.model.movies.Cinema
import com.mozie.databinding.FragmentScheduleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding
    private val viewModel: ScheduleViewModel by viewModels()

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.getCinemas()
    }

    private fun initObservers() {
        viewModel.cinemas.observe(viewLifecycleOwner, { cinemas ->
            handleCinemas(cinemas)
        })
    }

    private fun handleCinemas(cinemas: List<Cinema>) {
        val spinnerItems = mutableListOf<String>()
        spinnerItems.add(getString(R.string.cinema_spinner_empty))
        for (cinema in cinemas) {
            cinema.name?.let { spinnerItems.add(it) }
        }
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.item_spinner, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    showEmptyView()
                } else {
                    onCinemaSelected(cinemas[position])
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }
    }

    private fun onCinemaSelected(cinema: Cinema) {
        viewModel.getScreenings(cinema)
    }

    private fun showEmptyView() {
        // TODO
    }

}
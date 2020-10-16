package com.mozie.ui.tabSchedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mozie.R
import com.mozie.data.network.model.movies.Cinema
import com.mozie.databinding.FragmentScheduleBinding
import com.mozie.ui.Event
import com.mozie.ui.tabSchedule.models.ScheduleMovie
import com.mozie.ui.tabSchedule.models.ScheduleScreening
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.DateTime

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
        initViews()
        initObservers()
        viewModel.getCinemas()
    }

    private fun initViews() {
        binding.rvScreenings.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val spinnerItems = mutableListOf<String>()
        spinnerItems.add(getString(R.string.cinema_spinner_empty))
        setSpinnerAdapter(spinnerItems)
    }

    private fun initObservers() {
        viewModel.cinemas.observe(viewLifecycleOwner, { cinemas ->
            loadCinemas(cinemas)
        })
        viewModel.tabs.observe(viewLifecycleOwner, { tabs ->
            loadTabs(tabs)
        })
        viewModel.screenings.observe(viewLifecycleOwner, { screenings ->
            loadScreenings(screenings)
        })
        viewModel.networkError.observe(viewLifecycleOwner, { event ->
            handleError(event)
        })
    }

    private fun loadCinemas(cinemas: List<Cinema>) {
        val spinnerItems = mutableListOf<String>()
        spinnerItems.add(getString(R.string.cinema_spinner_empty))
        for (cinema in cinemas) {
            cinema.name?.let { spinnerItems.add(it) }
        }
        setSpinnerAdapter(spinnerItems)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    showEmptyView()
                } else {
                    onCinemaSelected(cinemas[position - 1])
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }
    }

    private fun loadScreenings(screenings: Map<ScheduleMovie, Map<String, List<ScheduleScreening>>>) {
        binding.rvScreenings.adapter = RvAdapter(screenings)
        showListView()
    }

    private fun loadTabs(tabs: List<Pair<String, DateTime>>) {
        binding.tabLayout.removeAllTabs()
        for (tab in tabs) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(tab.first))
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    onTabSelected(it)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // do nothing
            }
        })
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
        viewModel.onTabSelected(0)
    }

    private fun onCinemaSelected(cinema: Cinema) {
        showLoadingView()
        viewModel.onCinemaSelected(cinema)
    }

    private fun showEmptyView() {
        binding.emptyLayout.visibility = View.VISIBLE
        binding.tabLayout.visibility = View.GONE
        binding.rvScreenings.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showLoadingView() {
        binding.emptyLayout.visibility = View.GONE
        binding.tabLayout.visibility = View.GONE
        binding.rvScreenings.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showMovieLoadingView() {
        binding.emptyLayout.visibility = View.GONE
        binding.tabLayout.visibility = View.VISIBLE
        binding.rvScreenings.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showListView() {
        binding.emptyLayout.visibility = View.GONE
        binding.tabLayout.visibility = View.VISIBLE
        binding.rvScreenings.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun handleError(event: Event<String>) {
        Toast.makeText(
            context,
            event.getContentIfNotHandledOrReturnNull() ?: "",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setSpinnerAdapter(spinnerItems: List<String>) {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.item_spinner, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
    }

    private fun onTabSelected(index: Int) {
        viewModel.onTabSelected(index)
        showMovieLoadingView()
    }

}
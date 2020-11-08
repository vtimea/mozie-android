package com.mozie.ui.ticketPicker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mozie.R
import com.mozie.data.network.model.cinemas.Screening
import com.mozie.databinding.ActivityTicketPickerBinding
import com.mozie.ui.Event
import com.mozie.ui.ticketPicker.seatpicker.SeatPickerFragment
import com.mozie.ui.ticketPicker.summary.SummaryFragment
import com.mozie.ui.ticketPicker.ticketType.TicketTypeFragment
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.DateTime

@AndroidEntryPoint
class TicketPickerActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MOVIE_ID = "EXTR_MOVIEID"
        const val EXTRA_MOVIE_TITLE = "EXTR_MOVIE_TITLE"
    }

    private val viewModel: TicketPickerViewModel by viewModels()
    private lateinit var binding: ActivityTicketPickerBinding

    private lateinit var movieId: String
    private lateinit var movieTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketPickerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        movieId = intent.getStringExtra(EXTRA_MOVIE_ID) ?: ""
        movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE) ?: ""
        viewModel.getScreenings(movieId)
        initViews()
        initData()
    }

    private fun initViews() {
        binding.tvMovieTitle.text = movieTitle
        binding.pager.isUserInputEnabled = false
        binding.pager.adapter = FadingPagerAdapter(supportFragmentManager, lifecycle)
        binding.pager.setPageTransformer { page, position ->
            page.alpha = 0f
            page.visibility = View.VISIBLE
            page.animate()
                .alpha(1f)
        }
    }

    private fun initData() {
        viewModel.networkError.observe(this, {
            handleError(it)
        })
        viewModel.currentCinemas.observe(this, {
            onCurrentCinemas(it)
        })
        viewModel.currentDates.observe(this, {
            onCurrentDates(it)
        })
        viewModel.currentTimes.observe(this, {
            onCurrentTimes(it)
        })
        viewModel.currentScreening.observe(this, {
            onCurrentScreening(it)
        })
    }

    private inner class FadingPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        val fragments = listOf(
            TicketTypeFragment.newInstance(),
            SeatPickerFragment.newInstance(),
            SummaryFragment.newInstance()
        )

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    private fun handleError(event: Event<String>) {
        Toast.makeText(this, event.getContentIfNotHandledOrReturnNull(), Toast.LENGTH_SHORT).show()
    }

    private fun setSpinnerAdapter(spinnerItems: List<String>, spinner: Spinner) {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun onCurrentCinemas(cinemas: List<String>) {
        enableActionButton(cinemas.isNotEmpty())
        val spinnerItems = resources.getStringArray(R.array.cinema_spinner_empty).toMutableList()
        spinnerItems.addAll(cinemas)
        setSpinnerAdapter(spinnerItems, binding.spCinema)
        binding.spCinema.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var selectedCinema: String? = null
                    if (position > 0) {
                        selectedCinema = cinemas[position - 1]
                    }
                    viewModel.onCinemaSelected(selectedCinema)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // do nothing
                }
            }
    }

    private fun onCurrentDates(dates: List<DateTime>) {
        enableActionButton(dates.isNotEmpty())
        val spinnerItems = resources.getStringArray(R.array.spinner_empty).toMutableList()
        dates.forEach { date ->
            val text = getString(
                R.string.year_month_day_format,
                date.year,
                date.monthOfYear,
                date.dayOfMonth
            )
            spinnerItems.add(text)
        }
        setSpinnerAdapter(spinnerItems, binding.spDay)
        binding.spDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                var date: DateTime? = null
                if (position > 0) {
                    date = dates[position - 1]
                }
                viewModel.onDateSelected(date)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }
    }

    private fun onCurrentTimes(it: List<DateTime>) {
        enableActionButton(it.isNotEmpty())
        val spinnerItems = resources.getStringArray(R.array.spinner_empty).toMutableList()
        it.forEach { date ->
            val text = getString(R.string.hour_minute_format, date.hourOfDay, date.minuteOfHour)
            spinnerItems.add(text)
        }
        setSpinnerAdapter(spinnerItems, binding.spTime)
        binding.spTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                var date: DateTime? = null
                if (position > 0) {
                    date = it[position - 1]
                }
                viewModel.onTimeSelected(date)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }
    }

    private fun onCurrentScreening(screening: Screening?) {
        enableActionButton(screening != null)
        screening?.let {
            Toast.makeText(this, it.id, Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableActionButton(isEnabled: Boolean) {
        binding.btnNext.isEnabled = isEnabled
        if (isEnabled) {
            binding.btnNext.alpha = 1f
        } else {
            binding.btnNext.alpha = 0.4f
        }
    }
}
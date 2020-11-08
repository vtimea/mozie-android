package com.mozie.ui.ticketPicker

import android.os.Bundle
import android.view.Gravity
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
import androidx.lifecycle.lifecycleScope
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mozie.R
import com.mozie.data.network.model.cinemas.Screening
import com.mozie.databinding.ActivityTicketPickerBinding
import com.mozie.ui.Event
import com.mozie.ui.ticketPicker.seatpicker.SeatPickerFragment
import com.mozie.ui.ticketPicker.summary.SummaryFragment
import com.mozie.ui.ticketPicker.ticketType.TicketTypeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.joda.time.DateTime

@AndroidEntryPoint
class TicketPickerActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MOVIE_ID = "EXTR_MOVIEID"
        const val EXTRA_MOVIE_TITLE = "EXTR_MOVIE_TITLE"
        const val ANIMATION_DURATION: Long = 400L
        const val ANIMATION_DELAY: Long = 100L
    }

    private val ticketViewModel: TicketViewModel by viewModels()
    private val ticketTypeViewModel: TicketTypeViewModel by viewModels()
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
        initViews()
        initObservers()
        loadData()
    }

    override fun onBackPressed() {
        when (binding.pager.currentItem) {
            0 -> {
                super.onBackPressed()
            }
            1 -> {
                binding.pager.currentItem = 0
            }
            else -> {
                binding.pager.currentItem = 1
            }
        }
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
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.toolbar.title = getString(R.string.toolbar_ticket_type_picker)
                    }
                    1 -> {
                        binding.toolbar.title = getString(R.string.toolbar_seat_picker)
                        if (binding.bottomCard.visibility == View.GONE) {
                            lifecycleScope.launch {
                                fadePayBtn(false)
                                delay(ANIMATION_DELAY)
                                toggleBottomCard(true)
                            }
                        }
                    }
                    else -> {
                        binding.toolbar.title = getString(R.string.toolbar_ticket_summary)
                        lifecycleScope.launch {
                            toggleBottomCard(false)
                            delay(ANIMATION_DELAY)
                            fadePayBtn(true)
                        }
                    }
                }
            }
        })
        binding.btnNext.setOnClickListener {
            when (binding.pager.currentItem) {
                0 -> {
                    binding.pager.currentItem = 1
                }
                1 -> {
                    binding.pager.currentItem = 2
                }
            }
        }
        binding.btnPay.setOnClickListener {
            if (binding.pager.currentItem == 2) {
                // TODO START PAYMENT
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        ticketViewModel.networkError.observe(this, {
            handleError(it)
        })
        ticketViewModel.currentCinemas.observe(this, {
            onCurrentCinemas(it)
        })
        ticketViewModel.currentDates.observe(this, {
            onCurrentDates(it)
        })
        ticketViewModel.currentTimes.observe(this, {
            onCurrentTimes(it)
        })
        ticketViewModel.currentScreening.observe(this, {
            onCurrentScreening(it)
        })
        ticketTypeViewModel.userSelectedTickets.observe(this, {
            enableActionButton(ticketViewModel.currentScreening.value != null && ticketTypeViewModel.getChosenTicketsSize() > 0)
        })
    }

    private fun loadData() {
        ticketViewModel.getScreenings(movieId)
        ticketTypeViewModel.getTicketTypes()
    }

    private inner class FadingPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        val frTicketType = TicketTypeFragment.newInstance()
        val frSeatPicker = SeatPickerFragment.newInstance()
        val frSummary = SummaryFragment.newInstance()

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> frTicketType
                1 -> frSeatPicker
                else -> frSummary
            }
        }
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
                    ticketViewModel.onCinemaSelected(selectedCinema)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // do nothing
                }
            }
    }

    private fun onCurrentDates(dates: List<DateTime>) {
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
                ticketViewModel.onDateSelected(date)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }
    }

    private fun onCurrentTimes(it: List<DateTime>) {
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
                ticketViewModel.onTimeSelected(date)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }
    }

    private fun onCurrentScreening(screening: Screening?) {
        ticketTypeViewModel.onTicketTypeChanged(screening?.type)
        enableActionButton(screening != null && ticketTypeViewModel.getChosenTicketsSize() > 0)
    }

    private fun enableActionButton(isEnabled: Boolean) {
        binding.btnNext.isEnabled = isEnabled
        if (isEnabled) {
            binding.btnNext.alpha = 1f
        } else {
            binding.btnNext.alpha = 0.4f
        }
    }

    private fun fadePayBtn(show: Boolean) {
        val transition = Fade()
        transition.duration = ANIMATION_DURATION
        transition.addTarget(binding.bottomCardPay)
        TransitionManager.beginDelayedTransition(binding.parent, transition)
        binding.bottomCardPay.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun toggleBottomCard(show: Boolean) {
        val transition = Slide(Gravity.BOTTOM)
        transition.duration = ANIMATION_DURATION
        transition.addTarget(binding.bottomCard)
        TransitionManager.beginDelayedTransition(binding.parent, transition)
        binding.bottomCard.visibility = if (show) View.VISIBLE else View.GONE
    }
}
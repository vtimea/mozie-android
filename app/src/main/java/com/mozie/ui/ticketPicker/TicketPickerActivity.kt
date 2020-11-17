package com.mozie.ui.ticketPicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
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
import com.braintreepayments.api.dropin.DropInActivity
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import com.mozie.R
import com.mozie.data.network.model.cinemas.Screening
import com.mozie.data.network.model.tickets.TicketOrder
import com.mozie.databinding.ActivityTicketPickerBinding
import com.mozie.ui.Event
import com.mozie.ui.home.HomeActivity
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
        const val REQUEST_CODE_DROPIN = 11
    }

    private val ticketViewModel: TicketFilterViewModel by viewModels()
    private val ticketTypeViewModel: TicketTypeViewModel by viewModels()
    private val screeningRoomViewModel: ScreeningRoomViewModel by viewModels()
    private val ticketPaymentViewModel: TicketPaymentViewModel by viewModels()
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
        if (ticketPaymentViewModel.loading.value == true) {
            return
        }
        when (binding.pager.currentItem) {
            0 -> {
                super.onBackPressed()
            }
            1 -> {
                binding.pager.currentItem = 0
            }
            else -> {
                (binding.pager.adapter as FadingPagerAdapter).frSeatPicker.setSeatPickerVisibility(
                    true
                )
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
                        enableActionButton(ticketViewModel.currentScreening.value != null && ticketTypeViewModel.getChosenTicketCount() > 0)
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
                        enableActionButton(screeningRoomViewModel.getSelectedSeatCount() == screeningRoomViewModel.getSeatCountLimit())
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
                    (binding.pager.adapter as FadingPagerAdapter).frSeatPicker.setSeatPickerVisibility(
                        false
                    )
                    binding.pager.currentItem = 2
                }
            }
        }
        binding.btnPay.setOnClickListener {
            if (binding.pager.currentItem == 2) {
                startPayment()
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.loadingLayout.setOnClickListener {
            // do nothing
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
            if (binding.pager.currentItem == 0) {
                val ticketCount = ticketTypeViewModel.getChosenTicketCount()
                screeningRoomViewModel.onTicketCountChange(ticketCount)
                enableActionButton(ticketViewModel.currentScreening.value != null && ticketCount > 0)
            }
        })
        screeningRoomViewModel.selectedSeats.observe(this, {
            if (binding.pager.currentItem == 1) {
                enableActionButton(screeningRoomViewModel.getSelectedSeatCount() == screeningRoomViewModel.getSeatCountLimit())
            }
        })
        ticketPaymentViewModel.showDropInUI.observe(this, { resp ->
            resp.getContentIfNotHandledOrReturnNull()?.clientToken?.let { it1 -> showDropInUI(it1) }
        })
        ticketPaymentViewModel.loading.observe(this, {
            showLoading(it)
        })
        ticketPaymentViewModel.paymentResultEvent.observe(this, {
            it.getContentIfNotHandledOrReturnNull()?.let { it1 -> onPaymentResultReceived(it1) }
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

    @SuppressLint("DefaultLocale")
    private fun onCurrentTimes(it: List<Pair<String, DateTime>>) {
        val spinnerItems = resources.getStringArray(R.array.spinner_empty).toMutableList()
        it.forEach { event ->
            val type = event.first
            val date = event.second
            val text =
                getString(
                    R.string.hour_minute_type_format,
                    date.hourOfDay,
                    date.minuteOfHour,
                    type.toUpperCase()
                )
            spinnerItems.add(text)
        }
        setSpinnerAdapter(spinnerItems, binding.spTime)
        binding.spTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                var event: Pair<String, DateTime>? = null
                if (position > 0) {
                    event = it[position - 1]
                }
                ticketViewModel.onTimeSelected(event?.second)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }
    }

    private fun onCurrentScreening(screening: Screening?) {
        ticketTypeViewModel.onTicketTypeChanged(screening?.type)
        enableActionButton(screening != null && ticketTypeViewModel.getChosenTicketCount() > 0)
        screeningRoomViewModel.getRoomForScreening(screening?.id)
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

    private fun startPayment() {
        val ticketOrder = TicketOrder()
        ticketOrder.ticketTypes = ticketTypeViewModel.getChosenTicketIds()
        ticketOrder.seats = screeningRoomViewModel.getSelectedSeatIds()
        ticketOrder.sumAmount = ticketTypeViewModel.getSumPrice()
        ticketPaymentViewModel.startPayment(ticketOrder)
    }

    private fun showDropInUI(clientToken: String) {
        val dropInRequest = DropInRequest().clientToken(clientToken)
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE_DROPIN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DROPIN) {
            if (data == null) {
                ticketPaymentViewModel.onDropInResultCanceled()
                return
            }
            when (resultCode) {
                RESULT_OK -> {
                    val result: DropInResult =
                        data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)!!
                    ticketPaymentViewModel.onDropInResultOK(result)
                }
                RESULT_CANCELED -> {
                    ticketPaymentViewModel.onDropInResultCanceled()
                }
                else -> {
                    val error = data.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
                    ticketPaymentViewModel.onDropInResultError(error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayout.visibility = View.VISIBLE
        } else {
            binding.loadingLayout.visibility = View.GONE
        }
    }

    private fun onPaymentResultReceived(successful: Boolean) {
        if (successful) {
            showSuccessDialog()
        } else {
            showErrorDialog()
        }
    }

    private fun showSuccessDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_payment_success)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        val btnOk: Button = dialog.findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra(HomeActivity.EXTRA_SHOW_TICKETS, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }

    private fun showErrorDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_payment_failed)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        val btnOk: Button = dialog.findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}
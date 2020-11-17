package com.mozie.ui.home

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mozie.R
import com.mozie.databinding.ActivityHomeBinding
import com.mozie.ui.tabMovies.MoviesFragment
import com.mozie.ui.tabProfile.ProfileFragment
import com.mozie.ui.tabSchedule.ScheduleFragment
import com.mozie.ui.tabTickets.TicketsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    companion object {
        val EXTRA_SHOW_TICKETS = "EXTRA_TO_TICKETS"
    }

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()

        if (intent.hasExtra(EXTRA_SHOW_TICKETS)) {
            binding.pager.currentItem = 2
        }
    }

    private fun initView() {
        binding.pager.isUserInputEnabled = false
        binding.pager.adapter = FadingPagerAdapter(supportFragmentManager, lifecycle)
        binding.pager.setPageTransformer { page, position ->
            page.alpha = 0f
            page.visibility = View.VISIBLE
            page.animate()
                .alpha(1f)
        }
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_theaters)
                    tab.text = getString(R.string.menu_title_movies)
                }
                1 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_date_range)
                    tab.text = getString(R.string.menu_title_schedule)
                }
                2 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_qrcode)
                    tab.text = getString(R.string.menu_title_tickets)
                }
                else -> {
                    tab.icon =
                        ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_outline)
                    tab.text = getString(R.string.menu_title_profile)
                }
            }
        }.attach()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon?.clearColorFilter()
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabIconColor =
                    ContextCompat.getColor(applicationContext, R.color.colorActiveTab)
                tab?.icon?.colorFilter = PorterDuffColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
            }
        })
        val tabIconColor =
            ContextCompat.getColor(applicationContext, R.color.colorActiveTab)
        binding.tabLayout.getTabAt(0)?.icon?.colorFilter =
            PorterDuffColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
    }

    private inner class FadingPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        val fragments = listOf(
            MoviesFragment.newInstance(),
            ScheduleFragment.newInstance(),
            TicketsFragment.newInstance(),
            ProfileFragment.newInstance()
        )

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}
package com.mozie.ui.details

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.mozie.R
import com.mozie.data.network.model.movies.MovieDetail
import com.mozie.databinding.ActivityMovieDetailsBinding
import com.mozie.ui.details.MovieDetailsActivity.KEYS.KEY_MOVIE_ID
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {
    object KEYS {
        const val KEY_MOVIE_ID = "key_movie_id"
    }

    private lateinit var binding: ActivityMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()
    private val loadingFragment: DetailLoadingFragment = DetailLoadingFragment()
    private val detailsFragment: DetailsFragment = DetailsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(view)
        initViews()
        initObservers()

        val movieId = intent.getStringExtra(KEY_MOVIE_ID) ?: ""
        viewModel.getDetails(movieId)
    }

    private fun initViews() {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.add(R.id.fragment, detailsFragment)
        ft.add(R.id.fragment, loadingFragment)
        ft.commit()
        showLoadingFragment()
    }

    private fun initObservers() {
        viewModel.detail.observe(this, {
            showDetailsFragment(it)
        })
        // TODO HANDLE ERROR
    }

    private fun showDetailsFragment(movie: MovieDetail) {
        val ft = supportFragmentManager.beginTransaction()
        detailsFragment.loadDetails(movie)
        ft.hide(loadingFragment)
        ft.show(detailsFragment)
        ft.commit()
    }

    private fun showLoadingFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.hide(detailsFragment)
        ft.show(loadingFragment)
        ft.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_change, R.anim.slide_down)
    }
}
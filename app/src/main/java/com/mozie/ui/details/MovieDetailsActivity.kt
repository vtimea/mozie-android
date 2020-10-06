package com.mozie.ui.details

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
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
        setContentView(view)
        initViews()
        initObservers()

        val movieId = intent.getStringExtra(KEY_MOVIE_ID) ?: ""
        viewModel.getDetails(movieId)
    }

    private fun initViews() {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.add(detailsFragment, "det")
        ft.add(loadingFragment, "loading")
        ft.commit()
        showLoadingFragment()
    }

    private fun initObservers() {
        viewModel.detail.observe(this, {
            showDetailsFragment(it)
        })
    }

    private fun showDetailsFragment(movie: MovieDetail) {
        //todo refactor to navgraph
        val ft = supportFragmentManager.beginTransaction()
        ft.hide(loadingFragment)
        ft.show(detailsFragment)
        ft.commit()
    }

    private fun showLoadingFragment() {
        //todo refactor to navgraph
        val ft = supportFragmentManager.beginTransaction()
        ft.hide(detailsFragment)
        ft.show(loadingFragment)
        ft.commit()
    }
}
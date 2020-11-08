package com.mozie.ui.tabMovies

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.mozie.R
import com.mozie.data.network.model.movies.FeaturedMovie
import com.mozie.databinding.FragmentMoviesBinding
import com.mozie.ui.Event
import com.mozie.ui.details.MovieDetailsActivity
import com.mozie.ui.details.MovieDetailsActivity.KEYS.KEY_MOVIE_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment : Fragment(), ItemClickListener<FeaturedMovie> {
    private lateinit var binding: FragmentMoviesBinding
    private val viewModel: MoviesViewModel by viewModels()

    companion object {
        fun newInstance() = MoviesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
        loadMovies()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.moviesNow.value.isNullOrEmpty()) {
            loadMovies()
        }
    }

    private fun shimmer(play: Boolean, layout: ShimmerFrameLayout) {
        if (play) {
            layout.visibility = View.VISIBLE
            layout.startShimmerAnimation()
        } else {
            layout.visibility = View.GONE
            layout.stopShimmerAnimation()
        }
    }

    private fun initViews() {
        binding.rvRecommended.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSoon.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNow.layoutManager = GridLayoutManager(context, 2)
        binding.swipeRefresh.setOnRefreshListener {
            loadMovies()
        }
    }

    private fun initObservers() {
        viewModel.moviesRecommended.observe(viewLifecycleOwner, { movies ->
            handleMovieRecommendations(movies)
        })
        viewModel.moviesNow.observe(viewLifecycleOwner, { movies ->
            handleMoviesNow(movies)
        })
        viewModel.moviesSoon.observe(viewLifecycleOwner, { movies ->
            handleMoviesSoon(movies)
        })
        viewModel.networkError.observe(viewLifecycleOwner, { event ->
            handleError(event)
        })
        viewModel.navEvent.observe(viewLifecycleOwner, {
            navigateToDetails(it.getContentIfNotHandledOrReturnNull())
        })
    }

    override fun onItemClicked(item: FeaturedMovie) {
        viewModel.onPosterClicked(item)
    }

    private fun showMovieLayout(isEmpty: Boolean, tvTitle: TextView, rvList: RecyclerView) {
        val visibility = if (isEmpty) {
            View.GONE
        } else {
            View.VISIBLE
        }
        tvTitle.visibility = visibility
        rvList.visibility = visibility
    }

    private fun navigateToDetails(movieId: String?) {
        movieId?.let {
            val intent = Intent(activity, MovieDetailsActivity::class.java)
            intent.putExtra(KEY_MOVIE_ID, movieId)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_up, R.anim.no_change)
        }
    }

    private fun loadMovies() {
        if (viewModel.moviesNow.value.isNullOrEmpty()) {
            shimmer(true, binding.shimmerRecommended)
            shimmer(true, binding.shimmerSoon)
            shimmer(true, binding.shimmerNow)
        }
        viewModel.getMovies()
    }

    private fun handleMovieRecommendations(movies: List<FeaturedMovie>) {
        binding.swipeRefresh.isRefreshing = false
        binding.rvRecommended.adapter = RvAdapter(movies, this)
        showMovieLayout(movies.isEmpty(), binding.tvRecommended, binding.rvRecommended)
        shimmer(false, binding.shimmerRecommended)
    }

    private fun handleMoviesNow(movies: List<FeaturedMovie>) {
        binding.swipeRefresh.isRefreshing = false
        binding.rvNow.adapter = RvAdapter(movies, this)
        showMovieLayout(movies.isEmpty(), binding.tvNow, binding.rvNow)
        shimmer(false, binding.shimmerNow)
    }

    private fun handleMoviesSoon(movies: List<FeaturedMovie>) {
        binding.swipeRefresh.isRefreshing = false
        binding.rvSoon.adapter = RvAdapter(movies, this)
        showMovieLayout(movies.isEmpty(), binding.tvSoon, binding.rvSoon)
        shimmer(false, binding.shimmerSoon)
    }

    private fun handleError(event: Event<String>) {
        binding.swipeRefresh.isRefreshing = false
        Toast.makeText(
            context,
            event.getContentIfNotHandledOrReturnNull() ?: "",
            Toast.LENGTH_SHORT
        ).show()
    }
}
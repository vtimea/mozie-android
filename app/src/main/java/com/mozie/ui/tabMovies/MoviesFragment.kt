package com.mozie.ui.tabMovies

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
import com.mozie.data.network.model.movies.Movie
import com.mozie.databinding.FragmentMoviesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment : Fragment(), ItemClickListener<Movie> {
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
        viewModel.getMovies()
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
        shimmer(true, binding.shimmerRecommended)
        shimmer(true, binding.shimmerSoon)
        shimmer(true, binding.shimmerNow)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getMovies()
        }
    }

    private fun initObservers() {
        viewModel.moviesRecommended.observe(viewLifecycleOwner, { movies ->
            binding.swipeRefresh.isRefreshing = false
            binding.rvRecommended.adapter = RvAdapter(movies, this)
            showMovieLayout(movies.isEmpty(), binding.tvRecommended, binding.rvRecommended)
            shimmer(false, binding.shimmerRecommended)
        })
        viewModel.moviesNow.observe(viewLifecycleOwner, { movies ->
            binding.swipeRefresh.isRefreshing = false
            binding.rvNow.adapter = RvAdapter(movies, this)
            showMovieLayout(movies.isEmpty(), binding.tvNow, binding.rvNow)
            shimmer(false, binding.shimmerNow)
        })
        viewModel.moviesSoon.observe(viewLifecycleOwner, { movies ->
            binding.swipeRefresh.isRefreshing = false
            binding.rvSoon.adapter = RvAdapter(movies, this)
            showMovieLayout(movies.isEmpty(), binding.tvSoon, binding.rvSoon)
            shimmer(false, binding.shimmerSoon)
        })
        viewModel.networkError.observe(viewLifecycleOwner, { event ->
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(
                context,
                event.getContentIfNotHandledOrReturnNull() ?: "",
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    override fun onItemClicked(item: Movie) {
        Toast.makeText(context, "${item.title} clicked!", Toast.LENGTH_SHORT).show()
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
}
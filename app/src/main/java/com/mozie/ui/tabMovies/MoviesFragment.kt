package com.mozie.ui.tabMovies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mozie.databinding.FragmentMoviesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment : Fragment() {
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
        initObservers()
        viewModel.getMovies()
    }

    private fun initObservers() {
        viewModel.moviesRecommended.observe(viewLifecycleOwner, { event ->
            //todo
            Log.i("dl0csh", "Recommended movies = ${event.size}")
        })
        viewModel.moviesNow.observe(viewLifecycleOwner, { event ->
            //todo
            Log.i("dl0csh", "Now movies = ${event.size}")
        })
        viewModel.moviesSoon.observe(viewLifecycleOwner, { event ->
            //todo
            Log.i("dl0csh", "Soon movies = ${event.size}")
        })
        viewModel.networkError.observe(viewLifecycleOwner, { event ->
            //todo
            Log.i("dl0csh", "Network error")
        })
    }
}
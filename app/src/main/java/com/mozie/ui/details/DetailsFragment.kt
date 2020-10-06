package com.mozie.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mozie.R
import com.mozie.data.network.model.movies.MovieDetail
import com.mozie.databinding.FragmentMovieDetailsBinding
import com.squareup.picasso.Picasso

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    fun initViews() {
        binding.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.rvActors.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    fun loadDetails(movie: MovieDetail) {
        binding.tvTitle.text = movie.title
        binding.tvGenre.text = movie.genre
        binding.tvDescription.text = movie.description
        binding.tvLength.text = getString(R.string.length_text, movie.length)
        val url = movie.posterUrl
        if (!url.isNullOrBlank()) {
            Picasso.get().load(url).fit().centerCrop().into(binding.ivPoster)
        }
        if (movie.actors.isNullOrEmpty()) {
            binding.tvActorsTitle.visibility = View.GONE
            binding.rvActors.visibility = View.GONE
        } else {
            binding.tvActorsTitle.visibility = View.VISIBLE
            binding.rvActors.visibility = View.VISIBLE
            binding.rvActors.adapter = ActorsRvAdapter(movie.actors)
        }
    }
}
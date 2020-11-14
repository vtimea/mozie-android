package com.mozie.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mozie.R
import com.mozie.data.network.model.movies.MovieDetail
import com.mozie.databinding.FragmentMovieDetailsBinding
import com.mozie.ui.ticketPicker.TicketPickerActivity
import com.squareup.picasso.Picasso

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailsBinding
    private var movie: MovieDetail? = null

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

    private fun initViews() {
        binding.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.rvActors.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.btnPurchase.setOnClickListener {
            val intent = Intent(requireActivity(), TicketPickerActivity::class.java)
            intent.putExtra(TicketPickerActivity.EXTRA_MOVIE_ID, movie!!.id)
            intent.putExtra(TicketPickerActivity.EXTRA_MOVIE_TITLE, movie!!.title)
            startActivity(intent)
        }
    }

    fun loadDetails(movie: MovieDetail) {
        this.movie = movie
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
        if (movie.isActive == true) {
            binding.btnPurchase.visibility = View.VISIBLE
        } else {
            binding.btnPurchase.visibility = View.GONE
        }
    }
}
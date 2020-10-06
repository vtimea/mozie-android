package com.mozie.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mozie.databinding.FragmentMovieDetailsShimmerBinding

class DetailLoadingFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailsShimmerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailsShimmerBinding.inflate(inflater, container, false)
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
        binding.shimmerLayout.startShimmerAnimation()
    }
}
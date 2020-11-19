package com.mozie.ui.tabProfile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.Profile
import com.mozie.R
import com.mozie.databinding.FragmentProfileBinding
import com.mozie.ui.login.LoginActivity
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        val profile = Profile.getCurrentProfile()
        val name =
            if (profile.middleName.isNotBlank()) {
                getString(
                    R.string.name_last_middle_first,
                    profile.lastName,
                    profile.middleName,
                    profile.firstName
                )
            } else {
                getString(R.string.name_last_first, profile.lastName, profile.firstName)
            }

        binding.tvName.text = name

        val imageUrl = profile.getProfilePictureUri(200, 200)
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.profile_placeholder)
                .into(binding.ivProfile)
        }

        binding.tvLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.putExtra(LoginActivity.EXTRA_LOGOUT, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        requireActivity().startActivity(intent)
    }
}
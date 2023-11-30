package com.example.vemprofut.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentSplashBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed(this::checkAuth, 3000)

    }

    private fun checkAuth() {
        auth = Firebase.auth

        if (auth.currentUser == null) {
            findNavController().navigate(R.id.action_splashFragment_to_navigation)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_homeLocadorFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
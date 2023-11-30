package com.example.vemprofut.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentRegisterJogadorBinding


class RegisterJogadorFragment : Fragment() {

    private var _binding: FragmentRegisterJogadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterJogadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
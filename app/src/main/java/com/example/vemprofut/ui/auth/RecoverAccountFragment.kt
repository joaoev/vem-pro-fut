package com.example.vemprofut.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentLoginBinding
import com.example.vemprofut.databinding.FragmentRecoverAccountBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class RecoverAccountFragment : Fragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initClicks()
    }

    private fun initClicks() {
        binding.btnRecuperarConta.setOnClickListener(){
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.etRecuperarContaEmail.text.toString().trim()

        if (email.isNotEmpty()) {
            binding.progressBarRecuperar.isVisible = true
            recuperarContaLocador(email)
        } else {
            Toast.makeText(requireContext(), "Nenhum campo pode estar vazio!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recuperarContaLocador(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Enviamos o link de recuperação para seu e-mail!", Toast.LENGTH_SHORT).show()
                }

                binding.progressBarRecuperar.isVisible = false
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.vemprofut.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentLoginBinding
import com.example.vemprofut.databinding.FragmentSplashBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initClicks()
    }

    private fun initClicks() {
       binding.tvLoginCriarConta.setOnClickListener{
           findNavController().navigate(R.id.action_loginFragment_to_registerLocadorFragment)
       }

        binding.tvLoginEsqueceuSenha.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
        }

        binding.btnLoginEntrar.setOnClickListener(){
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.etLoginEmail.text.toString().trim()
        val senha = binding.etLoginSenha.text.toString().trim()

        if (email.isNotEmpty() &&
            senha.isNotEmpty()) {

            binding.progressBarLogin.isVisible = true

            loginLocador(email, senha)

        } else {
            Toast.makeText(requireContext(), "Nenhum campo pode estar vazio!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginLocador(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_global_homeLocadorFragment)
                } else {
                    binding.progressBarLogin.isVisible = false
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
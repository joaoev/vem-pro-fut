package com.example.vemprofut.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentLoginBinding
import com.example.vemprofut.databinding.FragmentPerfilBinding
import com.example.vemprofut.databinding.FragmentPerfilLocadorBinding

import com.example.vemprofut.ui.auth.LoginFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class PerfilLocadorFragment : Fragment() {

    private var _binding: FragmentPerfilLocadorBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilLocadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        initClicks()
    }

    private fun initClicks() {
        binding.btnPerfilLocadorEditar.setOnClickListener(){
        }

        binding.btnPerfilLocadorExcluir.setOnClickListener(){
        }

        binding.btnPerfilLocadorSair.setOnClickListener(){
            showDialogSair()
        }
    }

    private fun showDialogSair() {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Confirmação")
            builder.setMessage("Quer Sair da sua conta?")


            builder.setPositiveButton("Sim") { _, _ ->
                logoutApp()
            }

            builder.setNegativeButton("Não") { _, _ ->
            }

            val dialog = builder.create()
            dialog.show()
    }

    private fun logoutApp() {
        auth.signOut()
        findNavController().navigate(R.id.action_homeLocadorFragment_to_navigation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
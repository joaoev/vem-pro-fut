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
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentLoginBinding
import com.example.vemprofut.databinding.FragmentSplashBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

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
           findNavController().navigate(R.id.action_loginFragment_to_registerJogadorFragment)
       }

        binding.tvLoginEsqueceuSenha.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
        }

        binding.btnLoginCriarContaLocador.setOnClickListener() {
            findNavController().navigate(R.id.action_loginFragment_to_registerLocadorFragment)
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

            login(email, senha)

        } else {
            Toast.makeText(requireContext(), "Nenhum campo pode estar vazio!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId = FirebaseHelper.getIdUser() ?: "null"
                    getTypeAccount(userId) { tipoDeConta ->
                        Toast.makeText(requireContext(), "Tipo: $tipoDeConta - Id: $userId", Toast.LENGTH_SHORT).show()

                        when (tipoDeConta) {
                            "locador" -> findNavController().navigate(R.id.action_global_appLocadorFragment)
                            "jogador" -> findNavController().navigate(R.id.action_global_appJogadorFragment)
                            else -> {
                                Toast.makeText(requireContext(), "Erro ao achar o tipo de conta", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    FirebaseHelper.validError(task.exception?.message ?: "")
                    Toast.makeText(requireContext(), task.exception?.message ?: "", Toast.LENGTH_SHORT).show()
                    binding.progressBarLogin.isVisible = false
                }
            }
    }


    private fun getTypeAccount(userId: String, callback: (String) -> Unit) {
        FirebaseHelper.getDatabase()
            .child("jogador")
            .child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        callback.invoke("jogador")
                    } else {
                        FirebaseHelper.getDatabase()
                            .child("locador")
                            .child(userId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        callback.invoke("locador")
                                    } else {
                                        callback.invoke("desconhecido")
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    callback.invoke("erro")
                                }
                            })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke("erro")
                }
            })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
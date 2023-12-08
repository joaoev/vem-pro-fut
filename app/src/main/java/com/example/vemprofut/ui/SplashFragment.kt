package com.example.vemprofut.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentSplashBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


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
        if (FirebaseHelper.isAutenticated() == null) {
            findNavController().navigate(R.id.action_splashFragment_to_navigation)
        } else {
            val userId = FirebaseHelper.getIdUser() ?: "null"
            getTypeAccount(userId) { tipoDeConta ->
//                Toast.makeText(
//                    requireContext(),
//                    "Tipo: ${tipoDeConta} - Id: $userId",
//                    Toast.LENGTH_SHORT
//                ).show()
                when (tipoDeConta) {
                    null -> findNavController().navigate(R.id.action_splashFragment_to_navigation)
                    "locador" -> findNavController().navigate(R.id.action_splashFragment_to_appLocadorFragment)
                    "jogador" -> findNavController().navigate(R.id.action_splashFragment_to_appJogadorFragment)
                }
            }
        }
    }




    private fun getTypeAccount(id: String, callback: (String?) -> Unit) {
        FirebaseHelper.getDatabase()
            .child("jogador")
            .child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val userId = dataSnapshot.child("tipoConta").getValue(String::class.java)
                        callback(userId)
                    } else {
                        FirebaseHelper.getDatabase()
                            .child("locador")
                            .child(id)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        val userId = dataSnapshot.child("tipoConta").getValue(String::class.java)
                                        callback(userId)
                                    } else {
//                                        Toast.makeText(
//                                            requireContext(),
//                                            "Nenhum dado do usuário encontrado",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
                                        callback(null)
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Toast.makeText(
                                        requireContext(),
                                        "FirebaseData, Erro ao obter dados do Firebase",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    callback(null)
                                }
                            })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "FirebaseData, Erro ao obter dados do Firebase",
                        Toast.LENGTH_SHORT
                    ).show()
                    callback(null)
                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
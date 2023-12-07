package com.example.vemprofut.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentPerfilJogadorBinding
import com.example.vemprofut.databinding.FragmentPerfilLocadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PerfilJogadorFragment : Fragment() {
    private var _binding: FragmentPerfilJogadorBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilJogadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        initClicks()
        getInfosJogador()
    }

    private fun initClicks() {
        binding.btnPerfilJogadorEditar.setOnClickListener(){
            findNavController().navigate(R.id.action_appJogadorFragment_to_editarPerfilJogadorFragment)
        }

        binding.btnPerfilJogadorExcluir.setOnClickListener(){
            showDialogExcluir()
        }

        binding.btnPerfilJogadorSair.setOnClickListener(){
            showDialogSair()
        }
    }

    private fun getInfosJogador() {
        FirebaseHelper.getDatabase()
            .child("jogador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        if (isAdded) {
                            binding.txtPerfilJogadorNome.text = dataSnapshot.child("fullname").getValue(String::class.java)
                            binding.txtPerfilJogadorNick.text = "@${dataSnapshot.child("nickname").getValue(String::class.java)}"
                            binding.txtPerfilJogadorEmail.text = dataSnapshot.child("email").getValue(String::class.java)

                            binding.imgPerfilJogador.load(dataSnapshot.child("urlImage").getValue(String::class.java)){
                                placeholder(R.drawable.add_image_bg)
                            }
                        }

                    } else {
                        if (isAdded) {
                            binding.txtPerfilJogadorNome.text = "Erro ao obter informações do Locador."
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), "FirebaseData, Erro ao obter dados do Firebase", Toast.LENGTH_SHORT)
                        .show()
                }
            })
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

    private fun showDialogExcluir() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Confirmação")
        builder.setMessage("Deseja realmente apagar sua conta?")


        builder.setPositiveButton("Sim") { _, _ ->
            deleteAccount()
        }

        builder.setNegativeButton("Não") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteAccount() {

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("jogador/foto_perfil/foto_perfil_${FirebaseHelper.getIdUser() ?: "erro"}.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                //deleta a imagem
                storageRef.delete()
                    .addOnSuccessListener {
                        //caso o jogador tenha foto
                        val credenciais = getEmailAndPassword()

                        FirebaseHelper
                            .getDatabase()
                            .child("jogador")
                            .child(FirebaseHelper.getIdUser() ?: "erro")
                            .removeValue()
                            .addOnSuccessListener {

                                Log.d("FirebaseDatabase", "Dados removidos com sucesso.")

                                val user = Firebase.auth.currentUser!!

                                if (user != null) {
                                    val credential = EmailAuthProvider
                                        .getCredential(credenciais?.get(0)?: "", credenciais?.get(1)?: "")

                                    user.reauthenticate(credential)
                                        .addOnCompleteListener { reauthTask ->
                                            if (reauthTask.isSuccessful) {
                                                user?.delete()
                                                    ?.addOnSuccessListener {
                                                        Log.d("FirebaseAuth", "Conta do usuário excluída com sucesso.")
                                                        findNavController().navigate(R.id.action_appJogadorFragment_to_navigation)
                                                    }
                                                    ?.addOnFailureListener { exception ->

                                                        Log.e("FirebaseAuth", "Erro ao excluir a conta do usuário: $exception")
                                                    }
                                            } else {

                                                Log.d("FirebaseAuth", "Falha ao autenticar.")
                                            }
                                        }
                                }



                            }
                            .addOnFailureListener { exception ->
                                Log.e("FirebaseDatabase", "Erro ao remover dados: $exception")
                            }

                    }.addOnFailureListener {
                        Log.d("JOGADOR", "Erro ao deletar a imagem.")
                    }

            }
            .addOnFailureListener { exception ->
                //caso o jogador não tenha foto
                val credenciais = getEmailAndPassword()

                FirebaseHelper
                    .getDatabase()
                    .child("jogador")
                    .child(FirebaseHelper.getIdUser() ?: "erro")
                    .removeValue()
                    .addOnSuccessListener {

                        Log.d("FirebaseDatabase", "Dados removidos com sucesso.")

                        val user = Firebase.auth.currentUser!!

                        if (user != null) {
                            val credential = EmailAuthProvider
                                .getCredential(credenciais?.get(0)?: "", credenciais?.get(1)?: "")

                            user.reauthenticate(credential)
                                .addOnCompleteListener { reauthTask ->
                                    if (reauthTask.isSuccessful) {
                                        user?.delete()
                                            ?.addOnSuccessListener {
                                                Log.d("FirebaseAuth", "Conta do usuário excluída com sucesso.")
                                                findNavController().navigate(R.id.action_appJogadorFragment_to_navigation)
                                            }
                                            ?.addOnFailureListener { exception ->

                                                Log.e("FirebaseAuth", "Erro ao excluir a conta do usuário: $exception")
                                            }
                                    } else {

                                        Log.d("FirebaseAuth", "Falha ao autenticar.")
                                    }
                                }
                        }



                    }
                    .addOnFailureListener { exception ->
                        Log.e("FirebaseDatabase", "Erro ao remover dados: $exception")
                    }

            }

            }


    private fun getEmailAndPassword(): ArrayList<String>? {
        var credenciais = ArrayList<String>()

        FirebaseHelper.getDatabase()
            .child("jogador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val email = dataSnapshot.child("email").getValue(String::class.java) ?: ""
                        credenciais.add(email)

                        val senha = dataSnapshot.child("password").getValue(String::class.java) ?: ""
                        credenciais.add(senha)
                    } else {

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("getEmailAndPassword", "Erro ao obter dados.")
                }
            })

        return credenciais
    }
    private fun logoutApp() {
        auth.signOut()
        findNavController().navigate(R.id.action_appJogadorFragment_to_navigation)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
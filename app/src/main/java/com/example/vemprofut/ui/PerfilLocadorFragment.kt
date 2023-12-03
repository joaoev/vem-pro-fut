package com.example.vemprofut.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentLoginBinding
import com.example.vemprofut.databinding.FragmentPerfilBinding
import com.example.vemprofut.databinding.FragmentPerfilLocadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import androidx.fragment.app.FragmentManager

import com.example.vemprofut.ui.auth.LoginFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

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
        getInfosLocador()
    }

    private fun initClicks() {

        binding.btnPerfilLocadorEditar.setOnClickListener(){
            findNavController().navigate(R.id.action_appLocadorFragment_to_editarPerfilLocadorFragment2)
        }


        binding.btnPerfilLocadorExcluir.setOnClickListener(){
            showDialogExcluir()
        }

        binding.btnPerfilLocadorSair.setOnClickListener(){
            showDialogSair()
        }
    }

    private fun getInfosLocador() {
        FirebaseHelper.getDatabase()
            .child("locador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Verifique se há dados antes de tentar recuperar
                    if (dataSnapshot.exists()) {

                        if (isAdded) {
                            binding.txtPerfilLocadorNome.text = dataSnapshot.child("fullname").getValue(String::class.java)
                            binding.txtPerfilLocadorCPF.text = dataSnapshot.child("cpf").getValue(String::class.java)
                            binding.txtPerfilLocadorTelefone.text = dataSnapshot.child("phone").getValue(String::class.java)
                            binding.txtPerfilLocadorEmail.text = dataSnapshot.child("email").getValue(String::class.java)
                            binding.txtPerfilLocadorNomeEmpresa.text = dataSnapshot.child("companyName").getValue(String::class.java)
                            binding.txtPerfilLocadorCNPJ.text = dataSnapshot.child("cnpj").getValue(String::class.java)

                            binding.imgPerfilLocador.load(dataSnapshot.child("urlImage").getValue(String::class.java)){
                                placeholder(R.drawable.add_image)
                            }

                        }



                    } else {
                        Toast.makeText(requireContext(), "Nenhum usuário encontrado", Toast.LENGTH_SHORT)
                            .show()
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

        val storageRef = storage.reference
        val imageRef = storageRef.child("locador/foto_perfil/foto_perfil_${FirebaseHelper.getIdUser()?:"null"}.jpg")

        imageRef.delete()
            .addOnSuccessListener {
                Log.d("FirebaseStorage", "Imagem excluída com sucesso.")

                val credenciais = getEmailAndPassword()

                FirebaseHelper
                    .getDatabase()
                    .child("locador")
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
                                                findNavController().navigate(R.id.action_appLocadorFragment_to_navigation)
                                            }
                                            ?.addOnFailureListener { exception ->

                                                Log.e("FirebaseAuth", "Erro ao excluir a conta do usuário: $exception")
                                            }
                                    } else {

                                        Toast.makeText(requireContext(), "Falha na reautenticação", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }



                    }
                    .addOnFailureListener { exception ->
                        Log.e("FirebaseDatabase", "Erro ao remover dados: $exception")
                    }



            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseStorage", "Erro ao excluir a imagem: $exception")
            }


    }

    private fun getEmailAndPassword(): ArrayList<String>? {
        var credenciais = ArrayList<String>()

        FirebaseHelper.getDatabase()
            .child("locador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val email = dataSnapshot.child("email").getValue(String::class.java) ?: ""
                        credenciais.add(email)

                        val senha = dataSnapshot.child("password").getValue(String::class.java) ?: ""
                        credenciais.add(senha)
                    } else {
                        Toast.makeText(requireContext(), "Nenhum usuário encontrado", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), "FirebaseData, Erro ao obter dados do Firebase", Toast.LENGTH_SHORT)
                        .show()
                }
            })

        return credenciais
    }

    private fun logoutApp() {
        auth.signOut()
        findNavController().navigate(R.id.action_appLocadorFragment_to_navigation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
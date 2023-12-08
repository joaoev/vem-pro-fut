package com.example.vemprofut.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentVerCampoLocadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class VerCampoLocadorFragment : Fragment() {

    private var _binding: FragmentVerCampoLocadorBinding? = null
    private val binding get() = _binding!!

    private var campo_id: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerCampoLocadorBinding.inflate(inflater, container, false)
        campo_id = arguments?.getString("campo_id")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
        getInfosCampo()
    }

    private fun initClicks() {
        binding.btnVerCampoLocadorEditar.setOnClickListener(){
            val meuFragment = EditarCampoFragment()

            val args = Bundle()
            args.putString("campo_id", campo_id)
            meuFragment.arguments = args

            val fragmentManager = parentFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.flAppLocador, meuFragment)
            transaction.commit()
        }

        binding.btnVerCampoLocadorExcluir.setOnClickListener(){
            showDialogExcluir()
        }

        binding.toobarVerCampoLocador.setOnClickListener(){
            val meuFragment = HomeLocadorFragment()
            val fragmentManager = parentFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.flAppLocador, meuFragment)
            transaction.commit()
        }
    }

    private fun getInfosCampo() {
        FirebaseHelper.getDatabase()
            .child("campos")
            .child(campo_id?:"null")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        var comodidades = ArrayList<Boolean>()
                        var dias = ArrayList<Boolean>()

                        if (isAdded) {
                            comodidades.add(dataSnapshot.child("parking").getValue(Boolean::class.java)?:false)
                            comodidades.add(dataSnapshot.child("locker_room").getValue(Boolean::class.java)?:false)
                            comodidades.add(dataSnapshot.child("pub").getValue(Boolean::class.java)?:false)

                            dias.add(dataSnapshot.child("seg").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("ter").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("qua").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("qui").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("sex").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("sab").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("dom").getValue(Boolean::class.java) ?: false)

                            val diasSemana = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom")

                            var diasSelecionados = ""

                            for ((index, dia) in dias.withIndex()) {
                                if (dia) {
                                    if (diasSelecionados.isNotEmpty()) {
                                        diasSelecionados += " - "
                                    }
                                    diasSelecionados += diasSemana[index]
                                }
                            }

                            val nomesComodidades = listOf("Estacionamento", "Vestiário", "Bar")
                            var comodidadesSelecionadas = ""

                            for ((index, comodidade) in comodidades.withIndex()) {
                                if (comodidade) {
                                    if (comodidadesSelecionadas.isNotEmpty()) {
                                        comodidadesSelecionadas += " - "
                                    }
                                    comodidadesSelecionadas += nomesComodidades[index]
                                }
                            }

                            var horaInicio = dataSnapshot.child("time_start").getValue(Int::class.java).toString()
                            var horaFim = dataSnapshot.child("time_end").getValue(Int::class.java).toString()

                            binding.txtVerCampoLocadorNome.text = dataSnapshot.child("local_name").getValue(String::class.java)
                            binding.txtVerCampoLocadorLoc.text = dataSnapshot.child("address").getValue(String::class.java)
                            binding.txtVerCampoLocadorDesc.text = dataSnapshot.child("description").getValue(String::class.java)
                            binding.txtVerCampoLocadorValor.text = "R$ ${dataSnapshot.child("hourly_rate").getValue(Double::class.java).toString()}"
                            binding.txtVerCampoLocadorCom.text = comodidadesSelecionadas
                            binding.txtVerCampoLocadorHora.text = "${horaInicio}h - ${horaFim}h"
                            binding.txtVerCampoLocadorDias.text = diasSelecionados


                            binding.imgVerCampoLocador.load(dataSnapshot.child("url_image").getValue(String::class.java)){
                                placeholder(R.drawable.add_image_bg_square)
                                error(R.drawable.add_image_bg_square)
                            }
                        }

                    } else {
                        if (isAdded) {
                            binding.txtVerCampoLocadorNome.text = "Erro ao obter informações."
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        FirebaseHelper.getDatabase()
            .child("campos")
            .child(campo_id?:"null")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        var comodidades = ArrayList<Boolean>()
                        var dias = ArrayList<Boolean>()

                        if (isAdded) {
                            comodidades.add(dataSnapshot.child("parking").getValue(Boolean::class.java)?:false)
                            comodidades.add(dataSnapshot.child("locker_room").getValue(Boolean::class.java)?:false)
                            comodidades.add(dataSnapshot.child("pub").getValue(Boolean::class.java)?:false)

                            dias.add(dataSnapshot.child("seg").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("ter").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("qua").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("qui").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("sex").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("sab").getValue(Boolean::class.java) ?: false)
                            dias.add(dataSnapshot.child("dom").getValue(Boolean::class.java) ?: false)

                            val diasSemana = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom")

                            var diasSelecionados = ""

                            for ((index, dia) in dias.withIndex()) {
                                if (dia) {
                                    if (diasSelecionados.isNotEmpty()) {
                                        diasSelecionados += " - "
                                    }
                                    diasSelecionados += diasSemana[index]
                                }
                            }

                            val nomesComodidades = listOf("Estacionamento", "Vestiário", "Bar")
                            var comodidadesSelecionadas = ""

                            for ((index, comodidade) in comodidades.withIndex()) {
                                if (comodidade) {
                                    if (comodidadesSelecionadas.isNotEmpty()) {
                                        comodidadesSelecionadas += " - "
                                    }
                                    comodidadesSelecionadas += nomesComodidades[index]
                                }
                            }

                            var horaInicio = dataSnapshot.child("time_start").getValue(Int::class.java).toString()
                            var horaFim = dataSnapshot.child("time_end").getValue(Int::class.java).toString()

                            binding.txtVerCampoLocadorNome.text = dataSnapshot.child("local_name").getValue(String::class.java)
                            binding.txtVerCampoLocadorLoc.text = dataSnapshot.child("address").getValue(String::class.java)
                            binding.txtVerCampoLocadorDesc.text = dataSnapshot.child("description").getValue(String::class.java)
                            binding.txtVerCampoLocadorValor.text = "R$ ${dataSnapshot.child("hourly_rate").getValue(Double::class.java).toString()}"
                            binding.txtVerCampoLocadorCom.text = comodidadesSelecionadas
                            binding.txtVerCampoLocadorHora.text = "${horaInicio}h - ${horaFim}h"
                            binding.txtVerCampoLocadorDias.text = diasSelecionados


                            binding.imgVerCampoLocador.load(dataSnapshot.child("url_image").getValue(String::class.java)){
                                placeholder(R.drawable.add_image_bg_square)
                                error(R.drawable.add_image_bg_square)
                            }
                        }

                    } else {
                        if (isAdded) {
                            binding.txtVerCampoLocadorNome.text = "Erro ao obter informações."
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        FirebaseHelper
    }

    private fun showDialogExcluir() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Confirmação")
        builder.setMessage("Deseja realmente deletar este campo?")


        builder.setPositiveButton("Sim") { _, _ ->
            deleteCampo()
        }

        builder.setNegativeButton("Não") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteCampo() {

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("campos/imagens_campos${campo_id ?: "erro"}.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                //deleta a imagem
                storageRef.delete()
                    .addOnSuccessListener {
                        //caso o campo tenha imagem
                        FirebaseHelper
                            .getDatabase()
                            .child("campos")
                            .child(campo_id ?: "erro")
                            .removeValue()
                            .addOnSuccessListener {
                                val meuFragment = HomeLocadorFragment()
                                val fragmentManager = parentFragmentManager
                                val transaction = fragmentManager.beginTransaction()
                                transaction.replace(R.id.flAppLocador, meuFragment)
                                transaction.commit()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("CAMPO", "Erro ao remover dados: $exception")
                            }

                    }.addOnFailureListener {
                        Log.d("CAMPO", "Erro ao deletar a imagem.")
                    }

            }
            .addOnFailureListener { exception ->
                //caso o campo não tenha foto
                FirebaseHelper
                    .getDatabase()
                    .child("campos")
                    .child(campo_id ?: "erro")
                    .removeValue()
                    .addOnSuccessListener {
                        Log.d("CAMPO", "Dados removidos com sucesso.")
                        val meuFragment = HomeLocadorFragment()
                        val fragmentManager = parentFragmentManager
                        val transaction = fragmentManager.beginTransaction()
                        transaction.replace(R.id.flAppLocador, meuFragment)
                        transaction.commit()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("CAMPO", "Erro ao remover dados: $exception")
                    }

            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.vemprofut.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.compose.ui.text.capitalize
import androidx.core.view.isVisible
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentVerCampoJogadorBinding
import com.example.vemprofut.databinding.FragmentVerCampoLocadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.example.vemprofut.model.Agendamentos
import com.example.vemprofut.model.Campo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class VerCampoJogadorFragment : Fragment() {
    private var _binding: FragmentVerCampoJogadorBinding? = null
    private val binding get() = _binding!!

    private lateinit var agendamento: Agendamentos

    private var campo_id: String = ""
    private var locador_id: String = ""
    private var nome_jogador: String = ""
    private var valor_hora: Double = 0.0
    private var hora_inicio = 0
    private var hora_fim = 0
    private var dias_semana_campo = ""
    private var dia_semana =""
    private var data_escolhida = ""
    private var hora_escolhida = 0
    private var id_do_campo_escolhido = ""
    private var agendamento_existe = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerCampoJogadorBinding.inflate(inflater, container, false)
        campo_id = arguments?.getString("campo_id")?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
        getInfosCampo()
    }

    private fun initClicks() {
        binding.btnVerCampoJogadorAgendar.setOnClickListener(){

            iniciarAgendamento()
        }


        binding.toobarVerCampoJogador.setOnClickListener(){
            val meuFragment = HomeJogadorFragment()

            val fragmentManager = parentFragmentManager

            val transaction = fragmentManager.beginTransaction()

            transaction.replace(R.id.flAppJogador, meuFragment)

            transaction.commit()
        }

        binding.btnVerCampoJogadorCal.setOnClickListener() {
            selecionarData()
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

                            dias_semana_campo = diasSelecionados

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

                            hora_inicio = horaInicio.toInt()
                            hora_fim = horaFim.toInt()

                            binding.txtVerCampoJogadorNome.text = dataSnapshot.child("local_name").getValue(String::class.java)
                            binding.txtVerCampoJogadorLoc.text = dataSnapshot.child("address").getValue(String::class.java)
                            binding.txtVerCampoJogadorDesc.text = dataSnapshot.child("description").getValue(String::class.java)
                            valor_hora = dataSnapshot.child("hourly_rate").getValue(Double::class.java)?: 0.0
                            binding.txtVerCampoJogadorValor.text = "R$ ${dataSnapshot.child("hourly_rate").getValue(Double::class.java).toString()}"
                            binding.txtVerCampoJogadorCom.text = comodidadesSelecionadas
                            binding.txtVerCampoJogadorHora.text = "${horaInicio}h - ${horaFim}h"
                            binding.txtVerCampoJogadorDias.text = diasSelecionados

                            locador_id = dataSnapshot.child("id_locador").getValue(String::class.java)?: "id_locador_null"

                            binding.imgVerCampoJogador.load(dataSnapshot.child("url_image").getValue(String::class.java)){
                                placeholder(R.drawable.add_image_bg_square)
                                error(R.drawable.add_image_bg_square)
                            }

                            FirebaseHelper.getDatabase()
                                .child("locador")
                                .child(locador_id ?:"null")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            if (isAdded) {
                                                binding.imgVerCampoJogadorEmpresa.load(dataSnapshot.child("urlImage").getValue(String::class.java)){
                                                    placeholder(R.drawable.add_image_bg_square)
                                                    error(R.drawable.add_image_bg_square)
                                                }

                                                binding.txtVerCampoJogadorEmpresa.text = dataSnapshot.child("companyName").getValue(String::class.java)

                                            }

                                        } else {
                                            if (isAdded) {
                                                binding.txtVerCampoJogadorNome.text = "Erro ao obter informações."
                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {

                                    }
                                })

                        }

                    } else {
                        if (isAdded) {
                            binding.txtVerCampoJogadorNome.text = "Erro ao obter informações."
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    private fun getInfosJogador() {
        FirebaseHelper.getDatabase()
            .child("jogador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        if (isAdded) {
                            nome_jogador = dataSnapshot.child("fullname").getValue(String::class.java)?:"nome_jogador_null"
                        }



                    } else {
                        Log.d("NOMEJOGADOR", "errroo")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    private fun selecionarData() {
        binding.apply {
            btnVerCampoJogadorCal.setOnClickListener {
                // create new instance of DatePickerFragment
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                // we have to implement setFragmentResultListener
                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {
                        val data = bundle.getString("SELECTED_DATE")
                        val diaSemana = bundle.getString("DIA_DA_SEMANA")
                        txtVerCampoJogadorData.text = data
                        txtVerCampoJogadorDiaSemana.text = diaSemana
                    }
                }

                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun iniciarAgendamento() {
        getInfosJogador()

        if (isAdded){
            var nomeCampo = binding.txtVerCampoJogadorNome.text.toString()
            var diaSemana = binding.txtVerCampoJogadorDiaSemana.text.toString()
            var data = binding.txtVerCampoJogadorData.text.toString()
            var preco = valor_hora
            var hora = binding.edtVerCampoJogadorHora.text.toString()

            var nomeJogador = nome_jogador
            Log.d("TESTE","Nome jogador é: $nomeJogador")

            var pagamento = ""
            var campoId = campo_id
            var locadorId = locador_id

            val radioButtonId = binding.rbGrupoVerCampoJogador.checkedRadioButtonId

            if (radioButtonId != -1) {

                val radioButton = binding.root.findViewById<RadioButton>(radioButtonId)
                val opcaoSelecionada = radioButton.text.toString()
                pagamento = opcaoSelecionada

            } else {
               pagamento = "pagamento_null"
            }


            Log.d("HORA", "ini: ${hora_inicio} - fim: ${hora_fim}")
            Log.d("DATA_AGENDAMENTO", data)
            dia_semana = diaSemana

            val diaSelecionadoLowerCase = dia_semana.lowercase(Locale.getDefault())
            val tresPrimeirasLetras = diaSelecionadoLowerCase.substring(0, 3)
            val diasDaSemanaDoCampoLowerCase = dias_semana_campo.lowercase(Locale.getDefault())



            val diaSelecionadoEstaIncluso = diasDaSemanaDoCampoLowerCase.contains(tresPrimeirasLetras)
            Log.d("VERIFICANDODIASELECIO", "diaSelecionadoLowerCase: ${diaSelecionadoLowerCase} # diasDaSemanaDoCampoLowerCase: ${diasDaSemanaDoCampoLowerCase} # ${diaSelecionadoEstaIncluso}")

            if (hora.isNotEmpty() && !data.equals("dd/mm/yyyy")) {

                if(diaSelecionadoEstaIncluso) {

                    if (hora.toInt() >= hora_inicio && hora.toInt() <= hora_fim) {
                        getInfosJogador()
                        agendamento = Agendamentos(
                            id_locador = locadorId,
                            id_campo = campoId,
                            nome_campo = nomeCampo,
                            dia_semana = diaSemana.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            },
                            data = data,
                            preco = preco,
                            hora = hora.toInt(),
                            nome_jogador = nomeJogador,
                            forma_pagamento = pagamento,
                            id_jogador = FirebaseHelper.getIdUser() ?: ""
                        )

                        data_escolhida = data
                        hora_escolhida = hora.toInt()
                        id_do_campo_escolhido = campo_id

                        verificarDataHoraDoAgendamento(data_escolhida, hora_escolhida, id_do_campo_escolhido, locador_id)


                    } else {
                        Toast.makeText(requireContext(), "Hora não esta no intervalo", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "${diaSemana} não disponível!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Data ou hora não escolhido!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun agendarHorario(agendamento: Agendamentos) {
        if (isAdded) {
            FirebaseHelper
                .getDatabase()
                .child("agendamentos")
                .child(locador_id)
                .child(agendamento.id)
                .setValue(agendamento)
                .addOnCompleteListener { agendamento ->
                    if (agendamento.isSuccessful) {
                        Toast.makeText(requireContext(), "Sucesso: Horário Reservado", Toast.LENGTH_SHORT).show()

                        val fragmentManager = parentFragmentManager

                        val transaction = fragmentManager.beginTransaction()

                        transaction.replace(R.id.flAppJogador, HomeJogadorFragment())

                        transaction.commit()

                    } else {
                        Toast.makeText(requireContext(), "Erro ao criar 1", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),"Erro ao criar 2", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun verificarDataHoraDoAgendamento(data: String, hora: Int, idCampo: String, idLocador: String) {
        var agendamentoExiste = false

        val agendamentosRef = FirebaseHelper.getDatabase()
            .child("agendamentos")
            .child(idLocador)
        agendamentosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (isAdded) {
                        var i = 0
                        for (agendamentoDataSnapShot in dataSnapshot.children) {
                            val agendamento: Agendamentos? =
                                agendamentoDataSnapShot.getValue(object : GenericTypeIndicator<Agendamentos>() {})
                            i++
                            Log.d("TESTE", "${agendamentoExiste}")
                            if (agendamento!!.data.equals(data) && agendamento.hora == hora) {
                                agendamentoExiste = true
                                Log.d("TESTE", "Tamanho agendamento ${i} ${agendamentoExiste}")
                                break
                            }
                        }
                    }

                    Log.d("TESTE", "agendamento é: ${agendamentoExiste}")
                    if (agendamentoExiste) {
                        Toast.makeText(
                            requireContext(),
                            "Erro: $data e $hora indisponível",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        agendarHorario(agendamento)
                    }
                } else {
                    Log.d("TESTE", "Nenhum agendamento encontrado")
                    // Se não houver agendamentos, você pode prosseguir com o agendamento
                    agendarHorario(agendamento)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TESTE", "Erro ao acessar o banco de dados")
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
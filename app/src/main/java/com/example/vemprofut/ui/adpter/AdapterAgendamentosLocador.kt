package com.example.vemprofut.ui.adpter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.model.Agendamentos
import com.example.vemprofut.model.Campo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class AdapterAgendamentosLocador(
    private val agendamentos : ArrayList<Agendamentos>,
    val campoSelecionado: (String) -> Unit
): RecyclerView.Adapter<AdapterAgendamentosLocador.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeCampo : TextView = itemView.findViewById(R.id.txtAgendamentosNomeCampo)
        val valorHora : TextView = itemView.findViewById(R.id.txtAgendamentosValor)
        val data : TextView = itemView.findViewById(R.id.txtAgendamentosData)
        val hora : TextView = itemView.findViewById(R.id.txtAgendamentosHora)
        val nomeJogador : TextView = itemView.findViewById(R.id.txtAgendamentosNomeJogador)
        val tipoPagamento : TextView = itemView.findViewById(R.id.txtAgendamentosPagamento)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterAgendamentosLocador.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_agendamento, parent, false)

        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AdapterAgendamentosLocador.MyViewHolder, position: Int) {
        val agendamento = agendamentos[position]

        holder.nomeCampo.text = agendamento.nome_campo
        holder.valorHora.text = "R$ ${agendamento.preco.toString()}"



        holder.data.text = "Data: ${agendamento.data} - Dia da semana: ${agendamento.dia_semana}"
        holder.nomeJogador.text = "Quem solicitou: ${agendamento.nome_jogador}"
        holder.hora.text = "Hora: ${agendamento.hora}:00h"
        holder.tipoPagamento.text = "Forma pagamento: ${agendamento.forma_pagamento}"
        holder.itemView.setOnClickListener(){
            campoSelecionado(agendamento.id)
        }
    }

    override fun getItemCount() = agendamentos.size
}
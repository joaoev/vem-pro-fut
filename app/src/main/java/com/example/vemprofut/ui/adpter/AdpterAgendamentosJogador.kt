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


class AdapterAgendamentosJogador(
    private val agendamentos : ArrayList<Agendamentos>,
): RecyclerView.Adapter<AdapterAgendamentosJogador.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeCampo : TextView = itemView.findViewById(R.id.txtAgendamentosNomeCampoJ)
        val valorHora : TextView = itemView.findViewById(R.id.txtAgendamentosValorJ)
        val data : TextView = itemView.findViewById(R.id.txtAgendamentosDataJ)
        val hora : TextView = itemView.findViewById(R.id.txtAgendamentosHoraJ)
        val tipoPagamento : TextView = itemView.findViewById(R.id.txtAgendamentosPagamentoJ)
        val status : TextView = itemView.findViewById(R.id.txtAgendamentosStatusJ)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterAgendamentosJogador.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_agendamento_jogador, parent, false)

        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AdapterAgendamentosJogador.MyViewHolder, position: Int) {
        val agendamento = agendamentos[position]

        holder.nomeCampo.text = agendamento.nome_campo
        holder.valorHora.text = "R$ ${agendamento.preco.toString()}"



        holder.data.text = "Data: ${agendamento.data} - Dia da semana: ${agendamento.dia_semana}"
        holder.hora.text = "Hora: ${agendamento.hora}:00h"
        holder.tipoPagamento.text = "Forma pagamento: ${agendamento.forma_pagamento}"
        var status = false

        if (agendamento.foi_aceito) {
            status = true
        }

        var aceito = "Negado"

        if (status) {
            aceito = "Aceito"
        }
        holder.status.text = "Status: ${aceito}"
    }

    override fun getItemCount() = agendamentos.size
}
package com.example.vemprofut.model

import android.os.Parcelable
import com.example.vemprofut.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Agendamentos(
    var id: String = "",
    var id_jogador: String = "",
    var id_locador: String= "",
    var id_campo: String = "",
    var nome_campo: String= "",
    var dia_semana: String = "",
    var data: String = "01/01/2024)",
    var preco: Double = 0.0,
    var hora : Int = 0,
    var nome_jogador: String = "",
    var forma_pagamento: String = "desconhecido",
    var foi_pago: Boolean = false,
    var foi_aceito: Boolean = false
    ) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }

}

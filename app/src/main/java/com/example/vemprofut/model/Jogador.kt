package com.example.vemprofut.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Jogador(
    var id: String = "",
    var fullname: String,
    var nickname: String,
    var email: String,
    var password: String,
    var city: String = "",
    var state: String = "",
    var tipoConta: String = "jogador",
    var urlImage: String = "null"
) : Parcelable {
}
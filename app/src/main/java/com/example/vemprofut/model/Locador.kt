package com.example.vemprofut.model

import android.os.Parcelable
import com.example.vemprofut.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Locador(var id: String = "",
                   var fullname: String,
                   var cpf: String,
                   var companyName: String,
                   var cnpj: String,
                   var email: String,
                   var phone: String,
                   var password: String,
                   var address: String = "",
                   var city: String = "",
                   var state: String = "",
                   var tipoConta: String = "locador",
                   var urlImage: String = "null",
    var latitude: Double = 1.0,
    var longitude: Double = 1.0
) : Parcelable {
}

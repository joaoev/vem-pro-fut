package com.example.vemprofut.model

import android.os.Parcelable
import com.example.vemprofut.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class CampoData( var id: String,
                      var id_locators: Int,
                      var photo_of_the_place: String,
                      var local_name: String,
                      var address: String,
                      var description: String,
                      var hourly_rate: Double,
                      var parking: String,
                      var locker_room: String,
                      var pub: String) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
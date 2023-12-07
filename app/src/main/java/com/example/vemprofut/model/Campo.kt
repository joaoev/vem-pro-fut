package com.example.vemprofut.model

import android.os.Parcelable
import com.example.vemprofut.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize
import java.net.Inet4Address

@Parcelize
data class Campo(var id: String = "",
                 var id_locador: String= "",
                 var local_name: String = "null",
                 var description: String = "",
                 var hourly_rate: Double = 10.0,
                 var parking: Boolean = false,
                 var locker_room: Boolean = false,
                 var pub: Boolean = false,
                 var url_image: String = "",
                 var latitude: Double = 90.0,
                 var longitude: Double = 180.0,
                 var time_start: Int = 0,
                 var time_end: Int = 0,
                 var seg: Boolean = false,
                 var ter: Boolean = false,
                 var qua: Boolean = false,
                 var qui: Boolean = false,
                 var sex: Boolean = false,
                 var sab: Boolean = false,
                 var dom: Boolean = false,
    var address: String = "null",
    var state: String = "",
    var city: String = "",

    ) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }

}
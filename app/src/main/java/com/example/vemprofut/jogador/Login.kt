package com.example.vemprofut.jogador


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager

import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.vemprofut.CadastroJogador
import com.example.vemprofut.CadastroLocador
import com.example.vemprofut.DBHelper
import com.example.vemprofut.LoginLocador
import com.example.vemprofut.MainActivity
import com.example.vemprofut.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.util.Locale


class Login : AppCompatActivity () {
    private lateinit var btnCriarContaJogador : Button
    private lateinit var btnCriarContaLocador : Button
    private lateinit var btnFazerLoginLocador : Button

    private lateinit var editTextLoginEmail: EditText
    private lateinit var editTextLoginSenha: EditText

    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var loc : Button
    private lateinit var locinfo : TextView
    private var myCity = ""
    private var myState = ""
    private var myCountry = ""
    private var myLatitude = ""
    private var myLongitude = ""
    private var myAddress = ""

    private lateinit var btnFazerLogin: Button
    private lateinit var db: DBHelper




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        editTextLoginEmail = findViewById(R.id.editTextEmailLogin)
        editTextLoginSenha = findViewById(R.id.editTextPasswordLogin)
        btnFazerLoginLocador  = findViewById(R.id.buttonLoginFazerLoginLocador)
        btnFazerLogin = findViewById(R.id.buttonEntrarLogin)
        
        loc = findViewById(R.id.loc)
        locinfo = findViewById(R.id.locinfo)

        db = DBHelper(this)

        btnCriarContaJogador = findViewById(R.id.buttonLoginCriarContaJogador)
        btnCriarContaLocador = findViewById(R.id.buttonLoginCriarContaLocador)

        btnFazerLogin.setOnClickListener() {
            val emailTexto = editTextLoginEmail.text.toString()
            val senhaTexto = editTextLoginSenha.text.toString()

            if (TextUtils.isEmpty(emailTexto) || TextUtils.isEmpty(senhaTexto)) {
                Toast.makeText(this, "Nenhum Campo pode estar vazio!", Toast.LENGTH_LONG).show()
            } else {
                val checkUser = db.getUserByEmailAndPassword(emailTexto, senhaTexto)

                if (checkUser == true) {
                    Toast.makeText(this, "Sucesso no login!", Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    val userID = db.getUserIdByEmail(emailTexto)
                    intent.putExtra("user_id", userID)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login ou senha incorretos!", Toast.LENGTH_LONG).show()
                }
            }
        }



        btnCriarContaJogador.setOnClickListener {
            val intent = Intent(this, CadastroJogador::class.java)
            startActivity(intent)
        }

        btnFazerLoginLocador.setOnClickListener {
            val intent = Intent(this, LoginLocador::class.java)
            startActivity(intent)
            finish()
        }
        
        loc.setOnClickListener {
            fetchLocation()

        }
        

        btnCriarContaLocador.setOnClickListener{
            val intent = Intent(this, CadastroLocador::class.java)
            startActivity(intent)
        }

    }

    private fun fetchLocation() {
        if (!isLocationAvailable()) {
            // Se a localização não está disponível, solicitar ao usuário para ativá-la
            Toast.makeText(this, "A localização não está disponível. Ative a localização e tente novamente.", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        val task: Task<Location> = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        task.addOnSuccessListener { location ->
            if(location != null) {
                Toast.makeText(this, "${location.latitude} e ${location.longitude}!", Toast.LENGTH_LONG).show()
                getLocalInfos(location.latitude, location.longitude)

            } else {
                Toast.makeText(this, "Erro ao obter a localização. Tente novamente.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isLocationAvailable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    private fun getLocalInfos(latitude: Double, longitude: Double) {
        try {
            val geoCoder = Geocoder(this, Locale.getDefault())
            val address = geoCoder.getFromLocation( latitude, longitude, 3)

            if(address != null) {
                myAddress = address[0].getAddressLine(0)
                myCountry = address[0].featureName
                myCity = address[0].locality

                locinfo.text = "#${myAddress} #\n?${myCity} ?\n @${myCountry}@"
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Loading", Toast.LENGTH_LONG).show()
        }
    }

}
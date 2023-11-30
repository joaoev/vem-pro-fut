package com.example.vemprofut

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.example.vemprofut.ui.auth.LoginFragment

class LoginLocador : AppCompatActivity() {
    private lateinit var editTextLoginEmail: EditText
    private lateinit var editTextLoginSenha: EditText

    private lateinit var btnVoltar : Button
    private lateinit var btnFazerLogin: Button

    private lateinit var db: DBHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_locador)

        editTextLoginEmail = findViewById(R.id.editTextEmailLoginLocador)
        editTextLoginSenha = findViewById(R.id.editTextPasswordLoginLocador)
        btnFazerLogin = findViewById(R.id.buttonEntrarLoginLocador)

        db = DBHelper(this)

        btnVoltar = findViewById(R.id.buttonVoltarLoginLocador)

        btnFazerLogin.setOnClickListener() {
            val emailTexto = editTextLoginEmail.text.toString()
            val senhaTexto = editTextLoginSenha.text.toString()

            if (TextUtils.isEmpty(emailTexto) || TextUtils.isEmpty(senhaTexto)) {
                Toast.makeText(this, "Nenhum Campo pode estar vazio!", Toast.LENGTH_LONG).show()
            } else {
                val checkUser = db.getLocatorByEmailAndPassword(emailTexto, senhaTexto)

                if (checkUser == true) {
                    Toast.makeText(this, "Sucesso no login!", Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, MainActivityLocador::class.java)

                    val userID = db.getLocatorIdByEmail(emailTexto)
                    intent.putExtra("user_id", userID)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login ou senha incorretos!", Toast.LENGTH_LONG).show()
                }
            }
        }



        btnVoltar.setOnClickListener {
            val intent = Intent(this, LoginFragment::class.java)
            startActivity(intent)
            finish()
        }
    }
}
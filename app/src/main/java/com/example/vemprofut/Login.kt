package com.example.vemprofut


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class Login : AppCompatActivity () {
    private lateinit var btnCriarContaJogador : Button
    private lateinit var btnCriarContaLocador : Button
    private lateinit var btnFazerLoginLocador : Button

    private lateinit var editTextLoginEmail: EditText
    private lateinit var editTextLoginSenha: EditText

    private lateinit var btnFazerLogin: Button
    private lateinit var db: DBHelper




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        editTextLoginEmail = findViewById(R.id.editTextEmailLogin)
        editTextLoginSenha = findViewById(R.id.editTextPasswordLogin)
        btnFazerLoginLocador  = findViewById(R.id.buttonLoginFazerLoginLocador)
        btnFazerLogin = findViewById(R.id.buttonEntrarLogin)

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

        btnCriarContaLocador.setOnClickListener{
            val intent = Intent(this, CadastroLocador::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {

        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 0) {
            // Se houver algo para voltar, execute o comportamento padrão (popBackStack)
            super.onBackPressed()
        } else {
            // Se não houver nada para voltar, exiba um diálogo de confirmação
            exibirDialogoDeConfirmacao()
        }
    }

    fun exibirDialogoDeConfirmacao() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmação")
            .setMessage("Deseja realmente fechar o aplicativo?")
            .setPositiveButton("Sim") { dialogInterface, _ ->
                // Se o usuário pressionar "Sim", feche o aplicativo
                dialogInterface.dismiss()
                finish()
            }
            .setNegativeButton("Não") { dialogInterface, _ ->
                // Se o usuário pressionar "Não", apenas feche o diálogo
                dialogInterface.dismiss()
            }
            .show()
    }
}
package com.example.vemprofut
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroJogador : AppCompatActivity() {
    private lateinit var editTextNome: EditText
    private lateinit var editTextNick: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextSenha: EditText
    private lateinit var editTextCidade: EditText
    private lateinit var editTextEstado: EditText
    private lateinit var btnCriarConta: Button
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadastro_jogador)

        editTextNome = findViewById(R.id.editTextCadastroJogadorNome)
        editTextNick = findViewById(R.id.editTextCadastroJogadorNick)
        editTextEmail = findViewById(R.id.editTextCadastroJogadorEmail)
        editTextSenha = findViewById(R.id.editTextCadastroJogadorSenha)
        editTextCidade = findViewById(R.id.editTextCadastroJogadorCidade)
        editTextEstado= findViewById(R.id.editTextCadastroJogadorEstado)

        btnCriarConta = findViewById(R.id.buttonCadastroJogador)

        db = DBHelper(this)

        btnCriarConta.setOnClickListener {
            val nomeTexto = editTextNome.text.toString()
            val nickTexto = editTextNick.text.toString()
            val emailTexto = editTextEmail.text.toString()
            val senhaTexto = editTextSenha.text.toString()
            val cidadeTexto = editTextCidade.text.toString().lowercase()
            val estadoTexto = editTextEstado.text.toString().uppercase()



            if (TextUtils.isEmpty(nomeTexto) ||
                TextUtils.isEmpty(nickTexto) ||
                TextUtils.isEmpty(emailTexto) ||
                TextUtils.isEmpty(senhaTexto) ||
                TextUtils.isEmpty(cidadeTexto) ||
                TextUtils.isEmpty(estadoTexto)) {

                Toast.makeText(this, "Nenhum Campo pode estar vazio!", Toast.LENGTH_LONG).show()
            } else {
                val saveData = db.insertUser(nomeTexto, nickTexto, emailTexto, senhaTexto, cidadeTexto, estadoTexto)

                if (saveData != -1L) {
                    Toast.makeText(this, "Conta Criada, Faça Login!", Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, Login::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Usuário Existe ou Outro Erro!", Toast.LENGTH_LONG).show()
                }
            }
        }

    }


}
package com.example.vemprofut
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroLocador : AppCompatActivity() {
    private lateinit var editTextNome: EditText
    private lateinit var editTextCNPJCPF: EditText
    private lateinit var editTextNomeEmpresa: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextTelefone: EditText
    private lateinit var editTextSenha: EditText
    private lateinit var editTextCidade: EditText
    private lateinit var editTextEstado: EditText
    private lateinit var btnCriarContaLocador: Button
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadastro_locador)

        editTextNome = findViewById(R.id.editTextCadastroLocadorNome)
        editTextCNPJCPF = findViewById(R.id.editTextCadastroLocadorCNPJ)
        editTextNomeEmpresa = findViewById(R.id.editTextCadastroLocadorNomeEmpresa)
        editTextEmail = findViewById(R.id.editTextCadastroLocadorEmail)
        editTextTelefone = findViewById(R.id.editTextCadastroLocadorTelefone)
        editTextSenha = findViewById(R.id.editTextCadastroLocadorSenha)
        editTextCidade = findViewById(R.id.editTextCadastroLocadorCidade)
        editTextEstado = findViewById(R.id.editTextCadastroLocadorEstado)

        btnCriarContaLocador = findViewById(R.id.buttonCadastroLocador)

        db = DBHelper(this)

        btnCriarContaLocador.setOnClickListener {
            val nomeTexto = editTextNome.text.toString()
            val cnpjcpfTexto = editTextCNPJCPF.text.toString()
            val nomeEmpresaText = editTextNomeEmpresa.text.toString()
            val emailTexto = editTextEmail.text.toString()
            val telefoneTexto = editTextTelefone.text.toString()
            val senhaTexto = editTextSenha.text.toString()
            val cidadeTexto = editTextCidade.text.toString().lowercase()
            val estadoTexto = editTextEstado.text.toString().uppercase()



            if (TextUtils.isEmpty(nomeTexto) ||
                TextUtils.isEmpty(cnpjcpfTexto) ||
                TextUtils.isEmpty(nomeEmpresaText) ||
                TextUtils.isEmpty(emailTexto) ||
                TextUtils.isEmpty(telefoneTexto) ||
                TextUtils.isEmpty(senhaTexto) ||
                TextUtils.isEmpty(cidadeTexto) ||
                TextUtils.isEmpty(estadoTexto)) {

                Toast.makeText(this, "Nenhum Campo pode estar vazio!", Toast.LENGTH_LONG).show()
            } else {
                val saveData = db.insertLocator(nomeTexto,cnpjcpfTexto,  nomeEmpresaText, emailTexto, telefoneTexto, senhaTexto, cidadeTexto, estadoTexto)

                if (saveData != -1L) {
                    Toast.makeText(this, "Conta Criada, Faça Login!", Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, LoginLocador::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Usuário Existe ou Outro Erro!", Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}
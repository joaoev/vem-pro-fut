package com.example.vemprofut

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class PerfilEditarFragment : Fragment() {

    private lateinit var db: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil_editar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userID: Int = arguments?.getInt(ARG_USER_ID, -1) ?: -1

        db = DBHelper(requireContext())

        val btnSalvar = view.findViewById<Button>(R.id.buttonPerfilEditarSalvar)

        val etNome = view.findViewById<EditText>(R.id.etPerfilEditarNome)
        val etNick = view.findViewById<EditText>(R.id.etPerfilEditarNick)
        val etEmail = view.findViewById<EditText>(R.id.etPerfilEditarEmail)
        val etSenha = view.findViewById<EditText>(R.id.etPerfilEditarSenha)
        val etCidade = view.findViewById<EditText>(R.id.etPerfilEditarCidade)
        val etEstado = view.findViewById<EditText>(R.id.etPerfilEditarEstado)

        val usuarioInfos = db.getUserById(userID)

        if (usuarioInfos != null) {
            etNome.text = Editable.Factory.getInstance().newEditable(usuarioInfos.fullname)
            etNick.text = Editable.Factory.getInstance().newEditable(usuarioInfos.nickname)
            etEmail.text = Editable.Factory.getInstance().newEditable(usuarioInfos.email)
            etSenha.text = Editable.Factory.getInstance().newEditable(usuarioInfos.password)
            etCidade.text = Editable.Factory.getInstance().newEditable(usuarioInfos.city)
            etEstado.text = Editable.Factory.getInstance().newEditable(usuarioInfos.state)

        } else {

        }

        btnSalvar.setOnClickListener {
            // Obtenha os novos valores dos campos de edição
            val novoNome = etNome.text.toString()
            val novoNick = etNick.text.toString()
            val novoEmail = etEmail.text.toString()
            val novaSenha = etSenha.text.toString()
            val novaCidade = etCidade.text.toString()
            val novoEstado = etEstado.text.toString()

            // Chame a função para atualizar as informações

            val sucesso = db.updateJogador(
                Jogador(novoNome, novoNick, novoEmail,novaSenha,novaCidade,novoEstado)
            )

            if (sucesso) {
                Toast.makeText(requireContext(), "Alterações salvas com sucesso", Toast.LENGTH_SHORT).show()
                val detalhes = Perfil.newInstance(userID)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, detalhes)
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Falha ao salvar as alterações", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val ARG_USER_ID = "user_id"
        fun newInstance(userID: Int): PerfilEditarFragment {
            val args = Bundle()
            args.putInt(ARG_USER_ID, userID)
            val fragment = PerfilEditarFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
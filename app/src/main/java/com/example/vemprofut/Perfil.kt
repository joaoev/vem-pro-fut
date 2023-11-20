package com.example.vemprofut

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class Perfil : Fragment() {

    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DBHelper(requireContext())

        val userID: Int? = arguments?.getInt("user_id", -1)

        val btnEditar = view.findViewById<Button>(R.id.buttonPerfilEditar)
        val btnExcluir = view.findViewById<Button>(R.id.buttonPerfilExcluir)
        val btnSair = view.findViewById<Button>(R.id.buttonPerfilSair)

        val nomeText = view.findViewById<TextView>(R.id.textViewPerfilNome)
        val nickText = view.findViewById<TextView>(R.id.textViewPerfilNick)
        val emailText = view.findViewById<TextView>(R.id.textViewPerfilEmail)
        val cidadeText = view.findViewById<TextView>(R.id.textViewPerfilCidade)
        val estadoText = view.findViewById<TextView>(R.id.textViewPerfilEstado)

        val jogadorInfos = db.getUserById(userID ?: -1)

        if (jogadorInfos != null) {
            val nomeJogador: String = jogadorInfos.fullname ?: "Nome: null"
            val nickJogador: String = jogadorInfos.nickname ?: "Nickname: null"
            val emailJogador: String = jogadorInfos.email ?: "E-mail: null"
            val cidadeJogador: String = jogadorInfos.city ?: "Cidade: null"
            val estadoJogador: String = jogadorInfos.state ?: "Estado: null"

            nomeText.text = "Nome: $nomeJogador"
            nickText.text = "Nickname $nickJogador"
            emailText.text = "E-mail: $emailJogador"
            cidadeText.text = "Cidade: $cidadeJogador"
            estadoText.text = "Estado: $estadoJogador"
        } else {
            nomeText.text = "Usuário não encontrado"
            nickText.text = ""
            emailText.text = ""
            cidadeText.text = ""
            estadoText.text = ""
        }

        btnEditar.setOnClickListener() {
            val detalhes = PerfilEditarFragment.newInstance(userID?: -1)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, detalhes)
                .addToBackStack(null)
                .commit()
        }

        btnExcluir.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Confirmação")
            builder.setMessage("Tem certeza que deseja excluir sua conta?")


            builder.setPositiveButton("Sim") { _, _ ->
                val sucesso = db.deleteUser(userID ?: -1)


                if (sucesso) {

                    Toast.makeText(requireContext(), "Conta excluída com sucesso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finishAffinity()

                } else {
                    Toast.makeText(requireContext(), "Falha ao excluir a conta", Toast.LENGTH_SHORT).show()
                }
            }


            builder.setNegativeButton("Não") { _, _ ->
                // Nenhuma ação necessária, usuário optou por não excluir
            }

            val dialog = builder.create()
            dialog.show()
        }

        btnSair.setOnClickListener() {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Confirmação")
            builder.setMessage("Quer Sair da sua conta?")


            builder.setPositiveButton("Sim") { _, _ ->
                val intent = Intent(requireContext(), Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finishAffinity()

            }

            builder.setNegativeButton("Não") { _, _ ->
            }

            val dialog = builder.create()
            dialog.show()
        }


    }

    companion object {
        private const val ARG_USER_ID = "user_id"
        fun newInstance(userID: Int): Perfil {
            val args = Bundle()
            args.putInt(ARG_USER_ID, userID)
            val fragment = Perfil()
            fragment.arguments = args
            return fragment
        }
    }

}
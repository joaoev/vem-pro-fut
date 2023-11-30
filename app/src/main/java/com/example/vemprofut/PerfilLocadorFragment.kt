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
import com.example.vemprofut.ui.auth.LoginFragment


class PerfilLocadorFragment : Fragment() {

    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil_locador, container, false)
        val userID: Int = arguments?.getInt(ARG_USER_ID, -1) ?: -1
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        db = DBHelper(requireContext())

        val userID: Int? = arguments?.getInt("user_id", -1)

        val btnEditar = view.findViewById<Button>(R.id.buttonPerfilEditarLocador)
        val btnExcluir = view.findViewById<Button>(R.id.buttonPerfilExcluirLocador)
        val btnSair = view.findViewById<Button>(R.id.buttonPerfilSairLocador)

        val nomeText = view.findViewById<TextView>(R.id.textViewPerfilNomeLocador)
        val emailText = view.findViewById<TextView>(R.id.textViewPerfilEmailLocador)
        val cidadeText = view.findViewById<TextView>(R.id.textViewPerfilCidadeLocador)
        val estadoText = view.findViewById<TextView>(R.id.textViewPerfilEstadoLocador)
        val cnpjText = view.findViewById<TextView>(R.id.textViewPerfilCNPJCPFLocador)
        val telefoneText = view.findViewById<TextView>(R.id.textViewPerfilTelefoneLocador)
        val nomeLocalText = view.findViewById<TextView>(R.id.textViewPerfilNomeLocalLocador)

        val locadorInfos = db.getLocatorById(userID ?: -1)

        if (locadorInfos != null) {
            val nomeLocador: String = locadorInfos.fullname ?: "Nome: null"
            val emailLocador: String = locadorInfos.email ?: "E-mail: null"
            val cidadeLocador: String = locadorInfos.city ?: "Cidade: null"
            val estadoLocador: String = locadorInfos.state ?: "Estado: null"
            val cnpjLocador: String = locadorInfos.cnpjcpf ?: "CNPJ/CPF: null"
            val telefoneLocador: String = locadorInfos.phone ?: "Telefone: null"
            val nomeLocalLocador: String = locadorInfos.companyName ?: "Nome do Local: null"

            nomeText.text = "Nome: $nomeLocador"
            emailText.text = "E-mail: $emailLocador"
            cidadeText.text = "Cidade: $cidadeLocador"
            estadoText.text = "Estado: $estadoLocador"
            cnpjText.text = "CNPJ/CPF: $cnpjLocador"
            telefoneText.text = "Telefone: $telefoneLocador"
            nomeLocalText.text = "Nome do Local: $nomeLocalLocador"
        } else {
            nomeText.text = "Usuário não encontrado"
            emailText.text = ""
            cidadeText.text = ""
            estadoText.text = ""
            cnpjText.text = ""
            telefoneText.text = ""
            nomeLocalText.text = ""
        }

        btnEditar.setOnClickListener(){
            val detalhes = PerfilLocadorEditarFragment.newInstance(userID?: -1)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutLocador, detalhes)
                .addToBackStack(null)
                .commit()
        }

        btnExcluir.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Confirmação")
            builder.setMessage("Tem certeza que deseja excluir sua conta?")


            builder.setPositiveButton("Sim") { _, _ ->
                val sucesso = db.deleteLocador(userID ?: -1)


                if (sucesso) {

                    Toast.makeText(requireContext(), "Conta excluída com sucesso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), LoginFragment::class.java)
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
                    val intent = Intent(requireContext(), LoginLocador::class.java)
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
        fun newInstance(userID: Int): PerfilLocadorFragment {
            val args = Bundle()
            args.putInt(ARG_USER_ID, userID)
            val fragment = PerfilLocadorFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
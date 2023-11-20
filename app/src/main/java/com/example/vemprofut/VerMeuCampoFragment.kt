package com.example.vemprofut

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class VerMeuCampoFragment : Fragment() {
    private lateinit var tvSeg: TextView
    private lateinit var tvTer: TextView
    private lateinit var tvQua: TextView
    private lateinit var tvQui: TextView
    private lateinit var tvSex: TextView
    private lateinit var tvSab: TextView
    private lateinit var tvDom: TextView


    private lateinit var db: DBHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ver_meu_campo, container, false)
        val campoId: Int = arguments?.getInt(ARG_CAMPO_ID, -1) ?: -1
        val userID: Int = arguments?.getInt(ARG_USER_ID, -1) ?: -1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val campoId: Int = arguments?.getInt(ARG_CAMPO_ID, -1) ?: -1
        val userID: Int = arguments?.getInt(ARG_USER_ID, -1) ?: -1

        db = DBHelper(requireContext())

        val btnEditar = view.findViewById<Button>(R.id.buttonVerMeuCampoEditar)
        val btnDeletar = view.findViewById<Button>(R.id.buttonVerMeuCampoDeletar)

        val nomeLocalText = view.findViewById<TextView>(R.id.textViewVerMeuCampoLocal)
        val enderecoText = view.findViewById<TextView>(R.id.textViewVerMeuCampoEndereco)
        val descricaoText = view.findViewById<TextView>(R.id.textViewVerMeuCampoDescricao)
        val precoHoraText = view.findViewById<TextView>(R.id.textViewVerMeuCampoPrecoHora)
        val vestiarioText = view.findViewById<TextView>(R.id.textViewVerMeuCampoVestiario)
        val estacionamentoText = view.findViewById<TextView>(R.id.textViewVerMeuCampoEstacionamento)
        val barText = view.findViewById<TextView>(R.id.textViewVerMeuCampoBar)

        tvSeg = view.findViewById(R.id.textViewVerMeuCampoHorarioSeg)
        tvTer = view.findViewById(R.id.textViewVerMeuCampoHorarioTer)
        tvQua = view.findViewById(R.id.textViewVerMeuCampoHorarioQua)
        tvQui = view.findViewById(R.id.textViewVerMeuCampoHorarioQui)
        tvSex = view.findViewById(R.id.textViewVerMeuCampoHorarioSex)
        tvSab = view.findViewById(R.id.textViewVerMeuCampoHorarioSab)
        tvDom = view.findViewById(R.id.textViewVerMeuCampoHorarioDom)


        val campoInfos = db.getCampoByIdAndLocatorId(campoId,userID)

        if (campoInfos != null) {

            val nomeLocal: String = campoInfos.local_name ?: "null"
            val endereco: String = campoInfos.address ?: "null"
            val descricao: String = campoInfos.description ?: "null"
            val precoHora: String = campoInfos.hourly_rate.toString() ?: "null"
            var vestiario: String = campoInfos.locker_room ?: "null"
            var estacionamento: String = campoInfos.parking ?: "null"
            var bar: String = campoInfos.pub ?: "Nome do Local: null"

            vestiario = if (vestiario.equals("true")) "Vestiário: Sim  " else "Vestiário: Não  "
            estacionamento = if (estacionamento.equals("true")) "Estacionamento: Sim  " else "Estacionamento: Não  "
            bar = if (bar.equals("true")) "Bar: Sim  " else "Bar: Não"


            nomeLocalText.text = "$nomeLocal"
            enderecoText.text = "$endereco"
            descricaoText.text = "$descricao"
            precoHoraText.text = "$precoHora"
            estacionamentoText.text = "$estacionamento"
            vestiarioText.text = "$vestiario"
            barText.text = "$bar"
        }

        val horariosInfos = db.getScheduleByCampoId(campoId)


        for (horarioInfo in horariosInfos) {
            when (horarioInfo.day_of_the_week) {
                "segunda" -> {
                    if (horarioInfo.cheked == 0) {
                        tvSeg.text = "Segunda-Feira: Não Adicionado"
                    } else {
                        tvSeg.text = "Segunda-Feira: Início: ${horarioInfo.start_time} | Fim: ${horarioInfo.end_time}"
                    }
                }
                "terca" -> {
                    if (horarioInfo.cheked == 0) {
                        tvTer.text = "Terça-Feira: Não Adicionado"
                    } else {
                        tvTer.text = "Terça-Feira: Início: ${horarioInfo.start_time} | Fim: ${horarioInfo.end_time}"
                    }
                }
                "quarta" -> {
                    if (horarioInfo.cheked == 0) {
                        tvQua.text = "Quarta-Feira: Não Adicionado"
                    } else {
                        tvQua.text = "Quarta-Feira: Início: ${horarioInfo.start_time} | Fim: ${horarioInfo.end_time}"
                    }
                }
                "quinta" -> {
                    if (horarioInfo.cheked == 0) {
                        tvQui.text = "Quinta-Feira: Não Adicionado"
                    } else {
                        tvQui.text = "Quinta-Feira: Início: ${horarioInfo.start_time} | Fim: ${horarioInfo.end_time}"
                    }
                }
                "sexta" -> {
                    if (horarioInfo.cheked == 0) {
                        tvSex.text = "Sexta-Feira: Não Adicionado"
                    } else {
                        tvSex.text = "Sexta-Feira: Início: ${horarioInfo.start_time} | Fim: ${horarioInfo.end_time}"
                    }
                }
                "sabado" -> {
                    if (horarioInfo.cheked == 0) {
                        tvSab.text = "Sábado: Não Adicionado"
                    } else {
                        tvSab.text = "Sábado: Início: ${horarioInfo.start_time} | Fim: ${horarioInfo.end_time}"
                    }
                }
                "domingo" -> {
                    if (horarioInfo.cheked == 0) {
                        tvDom.text = "Domingo: Não Adicionado"
                    } else {
                        tvDom.text = "Domingo: Início: ${horarioInfo.start_time} | Fim: ${horarioInfo.end_time}"
                    }
                }
            }
        }

        btnEditar.setOnClickListener() {
            val detalhesCampo = EditarMeuCampoFragment.newInstance(campoId, userID)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutLocador, detalhesCampo)
                .addToBackStack(null)
                .commit()
        }

        btnDeletar.setOnClickListener() {
            val deleteResult = db.deleteCampo(campoId, userID)

            if (deleteResult) {
                // Campo excluído com sucesso
                Toast.makeText(requireContext(), "Campo excluído com sucesso", Toast.LENGTH_SHORT).show()

                // Aqui você pode decidir o que fazer após excluir o campo, como navegar para outra tela
            } else {
                // Falha ao excluir o campo
                Toast.makeText(requireContext(), "Falha ao excluir o campo", Toast.LENGTH_SHORT).show()
            }

        }

    }

    companion object {
        private const val ARG_CAMPO_ID = "campo_id"
        private const val ARG_USER_ID = "user_id"
        fun newInstance(campoId: Int, userID: Int): VerMeuCampoFragment {
            val args = Bundle()
            args.putInt(ARG_CAMPO_ID, campoId)
            args.putInt(ARG_USER_ID, userID)

            val fragment = VerMeuCampoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
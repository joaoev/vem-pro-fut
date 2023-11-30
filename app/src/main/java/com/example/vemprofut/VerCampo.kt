package com.example.vemprofut

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class VerCampo : Fragment() {
    private lateinit var tvSeg: TextView
    private lateinit var tvTer: TextView
    private lateinit var tvQua: TextView
    private lateinit var tvQui: TextView
    private lateinit var tvSex: TextView
    private lateinit var tvSab: TextView
    private lateinit var tvDom: TextView


    private lateinit var db: DBHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ver_campo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val campoId: Int = arguments?.getInt(ARG_CAMPO_ID, -1) ?: -1
        val userID: Int = arguments?.getInt(ARG_USER_ID, -1) ?: -1

        db = DBHelper(requireContext())

        val btnAgendar = view.findViewById<Button>(R.id.btnVerCampoAgendar)

        val nomeLocalText = view.findViewById<TextView>(R.id.tvVerCampoNome)
        val enderecoText = view.findViewById<TextView>(R.id.tvVerCampoEnd)
        val descricaoText = view.findViewById<TextView>(R.id.tvVerCampoDes)
        val precoHoraText = view.findViewById<TextView>(R.id.tvVerCampoPre)
        val vestiarioText = view.findViewById<TextView>(R.id.tvVerCampoVes)
        val estacionamentoText = view.findViewById<TextView>(R.id.tvVerCampoEst)
        val barText = view.findViewById<TextView>(R.id.tvVerCampoBar)

        tvSeg = view.findViewById(R.id.tvVerCampoHoraSeg)
        tvTer = view.findViewById(R.id.tvVerCampoHoraTer)
        tvQua = view.findViewById(R.id.tvVerCampoHoraQua)
        tvQui = view.findViewById(R.id.tvVerCampoHoraQui)
        tvSex = view.findViewById(R.id.tvVerCampoHoraSex)
        tvSab = view.findViewById(R.id.tvVerCampoHoraSab)
        tvDom = view.findViewById(R.id.tvVerCampoHoraDom)


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

        btnAgendar.setOnClickListener() {
            val detalhes = AgendamentoFragment.newInstance(campoId, userID?: -1)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, detalhes)
                .addToBackStack(null)
                .commit()
        }

    }
    companion object {
        private const val ARG_CAMPO_ID = "campo_id"
        private const val ARG_USER_ID = "user_id"
        fun newInstance(campoId: Int, userID: Int): VerCampo {
            val args = Bundle()
            args.putInt(ARG_CAMPO_ID, campoId)
            args.putInt(ARG_USER_ID, userID)

            val fragment = VerCampo()
            fragment.arguments = args
            return fragment
        }
    }
}
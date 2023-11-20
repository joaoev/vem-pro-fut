package com.example.vemprofut

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class EditarMeuCampoFragment : Fragment() {

    private lateinit var cbSegunda: CheckBox
    private lateinit var cbTerca: CheckBox
    private lateinit var cbQuarta: CheckBox
    private lateinit var cbQuinta: CheckBox
    private lateinit var cbSexta: CheckBox
    private lateinit var cbSabado: CheckBox
    private lateinit var cbDomingo: CheckBox

    private lateinit var etSegHoraInicio: EditText
    private lateinit var etSegHoraFim: EditText
    private lateinit var etTerHoraInicio: EditText
    private lateinit var etTerHoraFim: EditText
    private lateinit var etQuaHoraInicio: EditText
    private lateinit var etQuaHoraFim: EditText
    private lateinit var etQuiHoraInicio: EditText
    private lateinit var etQuiHoraFim: EditText
    private lateinit var etSexHoraInicio: EditText
    private lateinit var etSexHoraFim: EditText
    private lateinit var etSabHoraInicio: EditText
    private lateinit var etSabHoraFim: EditText
    private lateinit var etDomHoraInicio: EditText
    private lateinit var etDomHoraFim: EditText

    private lateinit var editTextNomeLocal: EditText
    private lateinit var editTextEndereco: EditText
    private lateinit var editTextDescricao: EditText
    private lateinit var editTextNumberDecimalValorHora: EditText
    private lateinit var checkBoxEstacionamento: CheckBox
    private lateinit var checkBoxVestiario: CheckBox
    private lateinit var checkBoxBar: CheckBox

    private lateinit var db: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editar_meu_campo, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val campoId: Int = arguments?.getInt(ARG_CAMPO_ID, -1) ?: -1
        val userID: Int = arguments?.getInt(ARG_USER_ID, -1) ?: -1


        db = DBHelper(requireContext())

        val btnSalvar = view.findViewById<Button>(R.id.buttonEditarCampoSalvarAlteracoes)

        val nomeLocalText = view.findViewById<EditText>(R.id.etEditarCampoNome)
        val enderecoText = view.findViewById<EditText>(R.id.etEditarCampoEndereco)
        val descricaoText = view.findViewById<EditText>(R.id.etEditarCampoDescricao)
        val precoHoraText = view.findViewById<EditText>(R.id.etEditarCampoValorHora)
        val vestiarioText = view.findViewById<CheckBox>(R.id.checkBoxEditarCampoVestiario)
        val estacionamentoText = view.findViewById<CheckBox>(R.id.checkBoxEditarCampoEstacionamento)
        val barText = view.findViewById<CheckBox>(R.id.checkBoxEditarCampoBar)

        val cbSeg = view.findViewById<CheckBox>(R.id.cbSegHorarioEditar)
        val cbTer = view.findViewById<CheckBox>(R.id.cbTerHorarioEditar)
        val cbQua = view.findViewById<CheckBox>(R.id.cbQuaHorarioEditar)
        val cbQui = view.findViewById<CheckBox>(R.id.cbQuiHorarioEditar)
        val cbSex = view.findViewById<CheckBox>(R.id.cbSexHorarioEditar)
        val cbSab = view.findViewById<CheckBox>(R.id.cbSabHorarioEditar)
        val cbDom = view.findViewById<CheckBox>(R.id.cbDomHorarioEditar)

        val etSegInicio = view.findViewById<EditText>(R.id.etSegInicioHorarioEditar)
        val etSegFim = view.findViewById<EditText>(R.id.etSegFimHorarioEditar)

        val etTerInicio = view.findViewById<EditText>(R.id.etTerInicioHorarioEditar)
        val etTerFim = view.findViewById<EditText>(R.id.etTerFimHorarioEditar)

        val etQuaInicio = view.findViewById<EditText>(R.id.etQuaInicioHorarioEditar)
        val etQuaFim = view.findViewById<EditText>(R.id.etQuaFimHorarioEditar)

        val etQuiInicio = view.findViewById<EditText>(R.id.etQuiInicioHorarioEditar)
        val etQuiFim = view.findViewById<EditText>(R.id.etQuiFimHorarioEditar)

        val etSexInicio = view.findViewById<EditText>(R.id.etSexInicioHorarioEditar)
        val etSexFim = view.findViewById<EditText>(R.id.etSexFimHorarioEditar)

        val etSabInicio = view.findViewById<EditText>(R.id.etSabInicioHorarioEditar)
        val etSabFim = view.findViewById<EditText>(R.id.etSabFimHorarioEditar)

        val etDomInicio = view.findViewById<EditText>(R.id.etDomInicioHorarioEditar)
        val etDomFim = view.findViewById<EditText>(R.id.etDomFimHorarioEditar)


        val campoInfos = db.getCampoByIdAndLocatorId(campoId, userID)

        if (campoInfos != null) {
            nomeLocalText.text = Editable.Factory.getInstance().newEditable(campoInfos.local_name)
            enderecoText.text = Editable.Factory.getInstance().newEditable(campoInfos.address)
            descricaoText.text = Editable.Factory.getInstance().newEditable(campoInfos.description)
            precoHoraText.text = Editable.Factory.getInstance().newEditable(campoInfos.hourly_rate.toString())
            vestiarioText.isChecked = campoInfos.locker_room.toBoolean()
            estacionamentoText.isChecked = campoInfos.parking.toBoolean()
            barText.isChecked = campoInfos.pub.toBoolean()
        } else {

        }

        val horariosInfos = db.getScheduleByCampoId(campoId)


        // Preencher os valores dos CheckBoxes
        cbSeg.isChecked = horariosInfos.any { it.day_of_the_week == "segunda" && it.cheked == 1 }
        cbTer.isChecked = horariosInfos.any { it.day_of_the_week == "terca" && it.cheked == 1 }
        cbQua.isChecked = horariosInfos.any { it.day_of_the_week == "quarta" && it.cheked == 1 }
        cbQui.isChecked = horariosInfos.any { it.day_of_the_week == "quinta" && it.cheked == 1 }
        cbSex.isChecked = horariosInfos.any { it.day_of_the_week == "sexta" && it.cheked == 1 }
        cbSab.isChecked = horariosInfos.any { it.day_of_the_week == "sabado" && it.cheked == 1 }
        cbDom.isChecked = horariosInfos.any { it.day_of_the_week == "domingo" && it.cheked == 1 }

// Preencher os valores dos EditTexts
        etSegInicio.setText(horariosInfos.find { it.day_of_the_week == "segunda" }?.start_time.toString())
        etSegFim.setText(horariosInfos.find { it.day_of_the_week == "segunda" }?.end_time.toString())

        etTerInicio.setText(horariosInfos.find { it.day_of_the_week == "terca" }?.start_time.toString())
        etTerFim.setText(horariosInfos.find { it.day_of_the_week == "terca" }?.end_time.toString())

        etQuaInicio.setText(horariosInfos.find { it.day_of_the_week == "quarta" }?.start_time.toString())
        etQuaFim.setText(horariosInfos.find { it.day_of_the_week == "quarta" }?.end_time.toString())

        etQuiInicio.setText(horariosInfos.find { it.day_of_the_week == "quinta" }?.start_time.toString())
        etQuiFim.setText(horariosInfos.find { it.day_of_the_week == "quinta" }?.end_time.toString())

        etSexInicio.setText(horariosInfos.find { it.day_of_the_week == "sexta" }?.start_time.toString())
        etSexFim.setText(horariosInfos.find { it.day_of_the_week == "sexta" }?.end_time.toString())

        etSabInicio.setText(horariosInfos.find { it.day_of_the_week == "sabado" }?.start_time.toString())
        etSabFim.setText(horariosInfos.find { it.day_of_the_week == "sabado" }?.end_time.toString())

        etDomInicio.setText(horariosInfos.find { it.day_of_the_week == "domingo" }?.start_time.toString())
        etDomFim.setText(horariosInfos.find { it.day_of_the_week == "domingo" }?.end_time.toString())

        btnSalvar.setOnClickListener() {
            val nomeLocalTextoNovo = nomeLocalText.text.toString()
            val enderecoTextoNovo = enderecoText.text.toString()
            val descricaoTextoNovo = enderecoText.text.toString()
            val valorHoraTextNovo: String = precoHoraText.text.toString()
            var valorHoraNovo: Double
            var isCheckedEstacionamentoNovo = "false"
            var isCheckedVestiarioNovo = "false"
            var isCheckedBarNovo = "false"

            if (estacionamentoText.isChecked) {
                isCheckedEstacionamentoNovo = "true"
            }

            if (vestiarioText.isChecked) {
                isCheckedVestiarioNovo = "true"
            }

            if (barText.isChecked) {
                isCheckedBarNovo = "true"
            }



            try {
                valorHoraNovo = valorHoraTextNovo.toDouble()
            } catch (e: NumberFormatException) {
                Toast.makeText(
                    requireContext(),
                    "O valor da hora deve ser um número válido",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            var checkedSeg = false
            var checkedTer = false
            var checkedQua = false
            var checkedQui = false
            var checkedSex = false
            var checkedSab = false
            var checkedDom = false

            checkedSeg = cbSeg.isChecked
            checkedTer = cbTer.isChecked
            checkedQua = cbQua.isChecked
            checkedQui = cbQui.isChecked
            checkedSex = cbSex.isChecked
            checkedSab = cbSab.isChecked
            checkedDom = cbDom.isChecked

            // Inicializar os EditText
            etSegHoraInicio = view.findViewById(R.id.etSegInicioHorarioEditar)
            etSegHoraFim = view.findViewById(R.id.etSegFimHorarioEditar)
            etTerHoraInicio = view.findViewById(R.id.etTerInicioHorarioEditar)
            etTerHoraFim = view.findViewById(R.id.etTerFimHorarioEditar)
            etQuaHoraInicio = view.findViewById(R.id.etQuaInicioHorarioEditar)
            etQuaHoraFim = view.findViewById(R.id.etQuaFimHorarioEditar)
            etQuiHoraInicio = view.findViewById(R.id.etQuiInicioHorarioEditar)
            etQuiHoraFim = view.findViewById(R.id.etQuiFimHorarioEditar)
            etSexHoraInicio = view.findViewById(R.id.etSexInicioHorarioEditar)
            etSexHoraFim = view.findViewById(R.id.etSexFimHorarioEditar)
            etSabHoraInicio = view.findViewById(R.id.etSabInicioHorarioEditar)
            etSabHoraFim = view.findViewById(R.id.etSabFimHorarioEditar)
            etDomHoraInicio = view.findViewById(R.id.etDomInicioHorarioEditar)
            etDomHoraFim = view.findViewById(R.id.etDomFimHorarioEditar)

            val segHoraInicio = etSegHoraInicio.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val segHoraFim = etSegHoraFim.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val terHoraInicio = etTerHoraInicio.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val terHoraFim = etTerHoraFim.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val quaHoraInicio = etQuaHoraInicio.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val quaHoraFim = etQuaHoraFim.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val quiHoraInicio = etQuiHoraInicio.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val quiHoraFim = etQuiHoraFim.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val sexHoraInicio = etSexHoraInicio.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val sexHoraFim = etSexHoraFim.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val sabHoraInicio = etSabHoraInicio.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val sabHoraFim = etSabHoraFim.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val domHoraInicio = etDomHoraInicio.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val domHoraFim = etDomHoraFim.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0

            val listaHorarios = ArrayList<HorarioData>()

            listaHorarios.add(HorarioData(-1, "segunda", segHoraInicio, segHoraFim, if (checkedSeg) 1 else 0))
            listaHorarios.add(HorarioData(-1, "terca", terHoraInicio, terHoraFim, if (checkedTer) 1 else 0))
            listaHorarios.add(HorarioData(-1, "quarta", quaHoraInicio, quaHoraFim, if (checkedQua) 1 else 0))
            listaHorarios.add(HorarioData(-1, "quinta", quiHoraInicio, quiHoraFim, if (checkedQui) 1 else 0))
            listaHorarios.add(HorarioData(-1, "sexta", sexHoraInicio, sexHoraFim, if (checkedSex) 1 else 0))
            listaHorarios.add(HorarioData(-1, "sabado", sabHoraInicio, sabHoraFim, if (checkedSab) 1 else 0))
            listaHorarios.add(HorarioData(-1, "domingo", domHoraInicio, domHoraFim, if (checkedDom) 1 else 0))

            val saveData = db.updateCampo(
                campoId,
                userID,
                nomeLocalTextoNovo,
                enderecoTextoNovo,
                descricaoTextoNovo,
                valorHoraNovo,
                isCheckedVestiarioNovo,
                isCheckedEstacionamentoNovo,
                isCheckedBarNovo,
                listaHorarios)

            if (saveData) {
                Toast.makeText(requireContext(), "Dados Atualizados!", Toast.LENGTH_LONG)
                    .show()

                val detalhesCampo = VerMeuCampoFragment.newInstance(campoId, userID)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutLocador, detalhesCampo)
                    .addToBackStack(null)
                    .commit()

            } else {
                Toast.makeText(requireContext(), "Erro Ao Atualizar!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
    companion object {
        private const val ARG_CAMPO_ID = "campo_id"
        private const val ARG_USER_ID = "user_id"
        fun newInstance(campoId: Int, userID: Int): EditarMeuCampoFragment {
            val args = Bundle()
            args.putInt(ARG_CAMPO_ID, campoId)
            args.putInt(ARG_USER_ID, userID)

            val fragment = EditarMeuCampoFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
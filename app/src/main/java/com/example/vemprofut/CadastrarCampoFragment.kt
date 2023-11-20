package com.example.vemprofut

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast


class CadastrarCampoFragment : Fragment() {

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
    private lateinit var btnCadastrarCampo: Button

    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cadastrar_campo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userID: Int = arguments?.getInt("user_id", -1) ?: -1


        editTextNomeLocal = view.findViewById(R.id.editTextCadastrarCampoNomeDoLocal)
        editTextEndereco = view.findViewById(R.id.editTextCadastrarCampoEndereco)
        editTextDescricao = view.findViewById(R.id.editTextCadastrarCampoDescricao)
        editTextNumberDecimalValorHora = view.findViewById(R.id.editTextNumberDecimalCadastrarCampoValorDaHora)
        checkBoxEstacionamento = view.findViewById(R.id.checkBoxCadastrarCampoEstacionamento)
        checkBoxVestiario = view.findViewById(R.id.checkBoxCadastrarCampoVestiario)
        checkBoxBar = view.findViewById(R.id.checkBoxCadastrarCampoBar)

        btnCadastrarCampo = view.findViewById(R.id.buttonCadastrarCampo)

        // Inicializar as CheckBox
        cbSegunda = view.findViewById(R.id.cbSegHorario)
        cbTerca = view.findViewById(R.id.cbTerHorario)
        cbQuarta = view.findViewById(R.id.cbQuaHorario)
        cbQuinta = view.findViewById(R.id.cbQuiHorario)
        cbSexta = view.findViewById(R.id.cbSexHorario)
        cbSabado = view.findViewById(R.id.cbSabHorario)
        cbDomingo = view.findViewById(R.id.cbDomHorario)



        db = DBHelper(requireContext())

        btnCadastrarCampo.setOnClickListener() {
            val nomeLocalTexto = editTextNomeLocal.text.toString()
            val enderecoTexto = editTextEndereco.text.toString()
            val descricaoTexto = editTextDescricao.text.toString()
            val valorHoraText: String = editTextNumberDecimalValorHora.text.toString()
            var valorHora: Double
            var isCheckedEstacionamento = "false"
            var isCheckedVestiario = "false"
            var isCheckedBar = "false"

            if (checkBoxEstacionamento.isChecked) {
                isCheckedEstacionamento = "true"
            }

            if (checkBoxVestiario.isChecked) {
                isCheckedVestiario = "true"
            }

            if (checkBoxBar.isChecked) {
                isCheckedBar = "true"
            }



            try {
                valorHora = valorHoraText.toDouble()
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

            checkedSeg = cbSegunda.isChecked
            checkedTer = cbTerca.isChecked
            checkedQua = cbQuarta.isChecked
            checkedQui = cbQuinta.isChecked
            checkedSex = cbSexta.isChecked
            checkedSab = cbSabado.isChecked
            checkedDom = cbDomingo.isChecked

            // Inicializar os EditText
            etSegHoraInicio = view.findViewById(R.id.etSegInicioHorario)
            etSegHoraFim = view.findViewById(R.id.etSegFimHorario)
            etTerHoraInicio = view.findViewById(R.id.etTerInicioHorario)
            etTerHoraFim = view.findViewById(R.id.etTerFimHorario)
            etQuaHoraInicio = view.findViewById(R.id.etQuaInicioHorario)
            etQuaHoraFim = view.findViewById(R.id.etQuaFimHorario)
            etQuiHoraInicio = view.findViewById(R.id.etQuiInicioHorario)
            etQuiHoraFim = view.findViewById(R.id.etQuiFimHorario)
            etSexHoraInicio = view.findViewById(R.id.etSexInicioHorario)
            etSexHoraFim = view.findViewById(R.id.etSexFimHorario)
            etSabHoraInicio = view.findViewById(R.id.etSabInicioHorario)
            etSabHoraFim = view.findViewById(R.id.etSabFimHorario)
            etDomHoraInicio = view.findViewById(R.id.etDomInicioHorario)
            etDomHoraFim = view.findViewById(R.id.etDomFimHorario)

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

            if (TextUtils.isEmpty(nomeLocalTexto) ||
                TextUtils.isEmpty(enderecoTexto) ||
                TextUtils.isEmpty(descricaoTexto)
                ) {
                Toast.makeText(requireContext(), "Nenhum Campo pode estar vazio!", Toast.LENGTH_LONG)
                    .show()
            } else {
                val saveData =
                    db.insertLocalAndAmenities (userID, "null", nomeLocalTexto, enderecoTexto, descricaoTexto, valorHora, isCheckedEstacionamento, isCheckedVestiario, isCheckedBar, listaHorarios)

                if (saveData != -1L) {
                    Toast.makeText(requireContext(), "Campo cadastrado!", Toast.LENGTH_LONG)
                        .show()
                    // Substitua a navegação usando Intent por algo semelhante a isto:
                    editTextNomeLocal.text.clear()
                    editTextEndereco.text.clear()
                    editTextDescricao.text.clear()
                    editTextNumberDecimalValorHora.text.clear()

                    checkBoxEstacionamento.isChecked = false
                    checkBoxVestiario.isChecked = false
                    checkBoxBar.isChecked = false

                    cbSegunda.isChecked=false
                    cbTerca.isChecked = false
                    cbQuarta.isChecked = false
                    cbQuinta.isChecked = false
                    cbSexta.isChecked = false
                    cbSabado.isChecked = false
                    cbDomingo.isChecked = false

                    etSegHoraInicio.text.clear()
                    etSegHoraFim.text.clear()
                    etTerHoraInicio.text.clear()
                    etTerHoraFim.text.clear()
                    etQuaHoraInicio.text.clear()
                    etQuaHoraFim.text.clear()
                    etQuiHoraInicio.text.clear()
                    etQuiHoraFim.text.clear()
                    etSexHoraInicio.text.clear()
                    etSexHoraFim.text.clear()
                    etSabHoraInicio.text.clear()
                    etSabHoraFim.text.clear()
                    etDomHoraInicio.text.clear()
                    etDomHoraFim.text.clear()

                } else {
                    Toast.makeText(requireContext(), "Erro!", Toast.LENGTH_LONG)
                        .show()
                }
            }


        }
    }

}
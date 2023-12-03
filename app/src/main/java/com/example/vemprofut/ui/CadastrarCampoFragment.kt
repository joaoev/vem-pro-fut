package com.example.vemprofut.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.vemprofut.HorarioData
import com.example.vemprofut.R


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

    }
    }


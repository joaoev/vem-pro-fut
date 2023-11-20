package com.example.vemprofut

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.vemprofut.databinding.FragmentAgendamentoBinding
import com.example.vemprofut.databinding.FragmentCriarJogoBinding

class AgendamentoFragment : Fragment(R.layout.fragment_agendamento) {
    private var _binding: FragmentAgendamentoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agendamento, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var db = DBHelper(requireContext())

        val campoId: Int = arguments?.getInt(ARG_CAMPO_ID, -1) ?: -1
        val userID: Int = arguments?.getInt(ARG_USER_ID, -1) ?: -1

        val btnAgendar = view.findViewById<Button>(R.id.btnAgendarCampoConfirmar)
        val etHoraInicio = view.findViewById<EditText>(R.id.etHoraInicioJogo)
        val dataAgenda = view.findViewById<EditText>(R.id.etDataAgenda)





                _binding = FragmentAgendamentoBinding.bind(view)

        binding.apply {
            btnSelectDate.setOnClickListener {
                // create new instance of DatePickerFragment
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                // we have to implement setFragmentResultListener
                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {
                        val date = bundle.getString("SELECTED_DATE")
                        etDataAgenda.setText(date)
                    }
                }

                // show
                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
            }
        }



        btnAgendar.setOnClickListener() {

            val radioGroup = view.findViewById<RadioGroup>(R.id.rgDia)
            val selectedRadioButtonId = radioGroup!!.checkedRadioButtonId
            Toast.makeText(context, "Radio $selectedRadioButtonId", Toast.LENGTH_LONG).show()

                val selectedRadioButton = view.findViewById<RadioButton>(selectedRadioButtonId)
                val diaDaSemanaSelecionado = selectedRadioButton.text.toString()

                val dataA = dataAgenda.text.toString()
                val horaI = etHoraInicio.text.toString()
                val horaInicio = horaI.toInt()


                val horaFim = horaInicio + 1


                val novoAgendamentoId = db.insertAgendamento(userID, campoId, dataA, diaDaSemanaSelecionado, horaInicio, horaFim)

                if (novoAgendamentoId != -1L) {
                    Toast.makeText(context, "Agendamento Feito", Toast.LENGTH_LONG).show()
                    val detalhes = VerCampo.newInstance(campoId, userID)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, detalhes)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(context, "Erro Agendamento", Toast.LENGTH_LONG).show()
                }
            }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val ARG_CAMPO_ID = "campo_id"
        private const val ARG_USER_ID = "user_id"
        fun newInstance(campoId: Int, userID: Int): AgendamentoFragment {
            val args = Bundle()
            args.putInt(ARG_CAMPO_ID, campoId)
            args.putInt(ARG_USER_ID, userID)

            val fragment = AgendamentoFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
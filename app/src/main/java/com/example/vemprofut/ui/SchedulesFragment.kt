package com.example.vemprofut.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentHomeLocadorBinding
import com.example.vemprofut.databinding.FragmentSchedulesBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.example.vemprofut.model.Agendamentos
import com.example.vemprofut.model.Campo
import com.example.vemprofut.ui.adpter.AdapterAgendamentosLocador
import com.example.vemprofut.ui.adpter.AdapterCamposLocador
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener


class SchedulesFragment : Fragment() {

    private var _binding: FragmentSchedulesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewAgendamentos()
    }

    private fun initRecyclerViewAgendamentos() {
        if(isAdded) {
            binding.rvAgendamentos.layoutManager = LinearLayoutManager(context)
        }
        var agendamentos = ArrayList<Agendamentos>()

        FirebaseHelper.getDatabase()
            .child("agendamentos")
            .child(FirebaseHelper.getIdUser() ?: "null")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (agendamentoDataSnapShot in dataSnapshot.children) {
                            val agendamento: Agendamentos? = agendamentoDataSnapShot.getValue(object : GenericTypeIndicator<Agendamentos>() {})
                            if (agendamento != null && !agendamentos.contains(agendamento)) {
                                agendamentos.add(agendamento!!)
                            }
                        }

                        if(isAdded) {
                            binding.rvAgendamentos.adapter = AdapterAgendamentosLocador(agendamentos) { agendamento_id ->

                                val status = mapOf(
                                    "foi_aceito" to true
                                )

                                FirebaseHelper.getDatabase()
                                    .child("agendamentos")
                                    .child(FirebaseHelper.getIdUser() ?: "erro")
                                    .child(agendamento_id)
                                    .updateChildren(status)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Locador: Status Atualizado!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Locador: Falha ao atualizar status!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    } else {
                        if(isAdded) {
                            binding.txtAgendamentos.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
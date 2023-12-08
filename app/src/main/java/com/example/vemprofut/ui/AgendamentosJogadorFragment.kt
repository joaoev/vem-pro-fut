package com.example.vemprofut.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentAgendamentosJogadorBinding
import com.example.vemprofut.databinding.FragmentSchedulesBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.example.vemprofut.model.Agendamentos
import com.example.vemprofut.ui.adpter.AdapterAgendamentosJogador
import com.example.vemprofut.ui.adpter.AdapterAgendamentosLocador
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class AgendamentosJogadorFragment : Fragment() {

    private var _binding: FragmentAgendamentosJogadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgendamentosJogadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewAgendamentos()
    }

    private fun initRecyclerViewAgendamentos() {
        if (isAdded) {
            binding.rvAgendamentosJ.layoutManager = LinearLayoutManager(context)
        }

        val agendamentos = ArrayList<Agendamentos>()

        // Referência ao nó "agendamentos"
        val agendamentosRef = FirebaseHelper.getDatabase().child("agendamentos")

        agendamentosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(agendamentosSnapshot: DataSnapshot) {
                for (locadorSnapshot in agendamentosSnapshot.children) {
                    // Itera sobre os filhos do nó "agendamentos" (que são IDs de locadores)
                    for (agendamentoSnapshot in locadorSnapshot.children) {
                        val agendamento: Agendamentos? =
                            agendamentoSnapshot.getValue(object : GenericTypeIndicator<Agendamentos>() {})
                        if (agendamento != null && agendamento.id_jogador.equals(FirebaseHelper.getIdUser()?:"")) {
                            agendamentos.add(agendamento)
                        }
                    }
                }

                if (isAdded) {
                    if (agendamentos.isNotEmpty()) {
                        binding.rvAgendamentosJ.adapter = AdapterAgendamentosJogador(agendamentos)
                    } else {
                        binding.txtAgendamentosJ.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Tratar o erro, se necessário
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.vemprofut.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentHomeJogadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.example.vemprofut.model.Campo
import com.example.vemprofut.ui.adpter.AdapterCamposJogador
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener


class HomeJogadorFragment : Fragment() {
    private var _binding: FragmentHomeJogadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeJogadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewCampos()
    }

    private fun initRecyclerViewCampos() {
        if(isAdded) {
            binding.rvHomeJogador.layoutManager = LinearLayoutManager(context)
        }
        var campos = ArrayList<Campo>()

        FirebaseHelper.getDatabase()
            .child("campos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (campoDataSnapShot in dataSnapshot.children) {
                            val campo: Campo? = campoDataSnapShot.getValue(object : GenericTypeIndicator<Campo>() {})
                            if (campo != null && !campos.contains(campo)) {
                                    campos.add(campo!!)
                            }
                        }

                        if(isAdded) {
                            binding.rvHomeJogador.adapter = AdapterCamposJogador(campos) { campo_id ->

                                val meuFragment = VerCampoJogadorFragment()

                                val args = Bundle()
                                args.putString("campo_id", campo_id)
                                meuFragment.arguments = args

                                val fragmentManager = parentFragmentManager

                                val transaction = fragmentManager.beginTransaction()

                                transaction.replace(R.id.flAppJogador, meuFragment)

                                transaction.commit()
                            }
                        }
                    } else {
                        if(isAdded) {
                            binding.txtHomeJogadorC.visibility = View.VISIBLE
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
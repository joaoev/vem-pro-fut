package com.example.vemprofut.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.vemprofut.ui.adpter.AdapterCamposLocador
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentHomeLocadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.example.vemprofut.model.Campo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class HomeLocadorFragment : Fragment() {
    private var _binding: FragmentHomeLocadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeLocadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewCampos()
    }

    private fun initRecyclerViewCampos() {
        if(isAdded) {
        binding.rvHomeLocador.layoutManager = LinearLayoutManager(context)}
        var campos = ArrayList<Campo>()

        FirebaseHelper.getDatabase()
            .child("campos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (campoDataSnapShot in dataSnapshot.children) {
                            val campo: Campo? = campoDataSnapShot.getValue(object : GenericTypeIndicator<Campo>() {})
                            if (campo != null && !campos.contains(campo)) {
                                if (campo.id_locador.equals(FirebaseHelper.getIdUser() ?: "null")){
                                    campos.add(campo!!)
                                }

                            }
                        }

                        if(isAdded) {
                            binding.rvHomeLocador.adapter = AdapterCamposLocador(campos) { campo_id ->

                                val meuFragment = VerCampoLocadorFragment()

                                val args = Bundle()
                                args.putString("campo_id", campo_id)
                                meuFragment.arguments = args

                                val fragmentManager = parentFragmentManager

                                val transaction = fragmentManager.beginTransaction()

                                transaction.replace(R.id.flAppLocador, meuFragment)

                                transaction.commit()
                            }
                        }
                    } else {
                        if(isAdded) {
                            binding.txtHomeLocadorC.visibility = View.VISIBLE
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
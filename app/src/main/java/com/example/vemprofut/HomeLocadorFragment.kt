package com.example.vemprofut

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeLocadorFragment : Fragment(), AdapterCamposLocador.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_locador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var db = DBHelper(requireContext())
        val userID: Int = arguments?.getInt("user_id", -1) ?: -1

        Toast.makeText(context, "$userID", Toast.LENGTH_LONG).show()

        val campos = db.getAllCamposByLocatorId(userID)

        val itemAdapter=AdapterCamposLocador(campos, this)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewCamposLocador)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = itemAdapter

        recyclerView.setOnClickListener {

        }
    }

    override fun onItemClick(campoId: Int, userID: Int) {
        val detalhesCampo = VerMeuCampoFragment.newInstance(campoId, userID)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutLocador, detalhesCampo)
            .addToBackStack(null)
            .commit()
    }
}
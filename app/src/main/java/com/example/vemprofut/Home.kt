package com.example.vemprofut

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Home : Fragment() , Adapter.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var db = DBHelper(requireContext())
        val userID: Int = arguments?.getInt("user_id", -1) ?: -1
        Toast.makeText(context, "$userID", Toast.LENGTH_LONG).show()

        val userInfo = db.getUserById(userID)

        if (userInfo != null) {
            Toast.makeText(context, "${userInfo.city}", Toast.LENGTH_LONG).show()
        }
        if (userInfo != null) {
            val campos = db.getLocalsByLocatorsCityAndState(userInfo.city , userInfo.state)

            val itemAdapter=Adapter(campos, this)

            val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(context)

            recyclerView.adapter = itemAdapter

            recyclerView.setOnClickListener {

            }
        } else {

        }



    }

    override fun onItemClick(campoId: Int, userId: Int) {
        val detalhesCampo = VerCampo.newInstance(campoId, userId)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, detalhesCampo)
            .addToBackStack(null)
            .commit()
    }
}
package com.example.vemprofut.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vemprofut.AdapterCamposLocador
import com.example.vemprofut.DBHelper
import com.example.vemprofut.R
import com.example.vemprofut.VerMeuCampoFragment
import com.example.vemprofut.databinding.FragmentHomeLocadorBinding
import com.example.vemprofut.databinding.FragmentLoginBinding

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
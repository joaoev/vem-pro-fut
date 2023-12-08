package com.example.vemprofut.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentAppJogadorBinding

class AppJogadorFragment : Fragment() {

    private var _binding: FragmentAppJogadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppJogadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        replaceFragment(HomeJogadorFragment())

        binding.btnvMenuJogador.setOnItemSelectedListener {

            when(it.itemId){

                R.id.menuHomeJogador -> {
                    val homeFragment = HomeJogadorFragment()
                    replaceFragment(homeFragment)
                }


                R.id.menuAgendaJogador -> {
                    val mapaFragment = AgendamentosJogadorFragment()
                    replaceFragment(mapaFragment)
                }

                R.id.menuPerfilJogador -> {
                    val perfilFragment = PerfilJogadorFragment()
                    replaceFragment(perfilFragment)
                }

                else -> {
                }

            }

            true

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        // Use childFragmentManager para manipular fragments dentro de fragments
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Substituir o fragmento atual pelo novo fragmento
        fragmentTransaction.replace(R.id.flAppJogador, fragment)

        // Adicionar a transação à pilha de retrocesso
        //fragmentTransaction.addToBackStack(null)

        // Commit da transação
        fragmentTransaction.commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
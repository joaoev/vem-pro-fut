package com.example.vemprofut.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vemprofut.DashboardFragment
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentAppLocadorBinding

class AppLocadorFragment : Fragment() {

    private var _binding: FragmentAppLocadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppLocadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        replaceFragment(HomeLocadorFragment())

        binding.btnvMenuLocador.setOnItemSelectedListener {

            when(it.itemId){

                R.id.menuHomeLocador -> {

                    val homeFragment = HomeLocadorFragment()
                    replaceFragment(homeFragment)

                }
                R.id.menuDashboardLocador -> {

                    val dashboardFragment = DashboardFragment()
                    replaceFragment(dashboardFragment)
                }
                R.id.menuCamposLocador -> {
                    val camposFragment = CadastrarCampoFragment()
                    replaceFragment(camposFragment)
                }

                R.id.menuAgendamentosLocador -> {
                    val agendamentosFragment = SchedulesFragment()
                    replaceFragment(agendamentosFragment)
                }

                R.id.menuPerfilLocador -> {
                    val perfilFragment = PerfilLocadorFragment()
                    replaceFragment(perfilFragment)
                }


                else ->{



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
        fragmentTransaction.replace(R.id.flAppLocador, fragment)

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
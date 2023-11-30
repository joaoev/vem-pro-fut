package com.example.vemprofut.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vemprofut.CampoGPSFragment
import com.example.vemprofut.Jogos
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentHomeLocadorBinding

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

        replaceFragment(HomeAppLocadorFragment())

        binding.btnvMenuJogador.setOnItemSelectedListener {

            when(it.itemId){

                R.id.menuHome -> {

                        val homeFragment = HomeAppLocadorFragment()
//                        val args = Bundle()
//                        args.putInt("user_id", userID)
//                        homeFragment.arguments = args
                        replaceFragment(homeFragment)

                }
                R.id.menuMeusJogos -> {

                    val jogosFragment = Jogos()
//                    val args = Bundle()
//                    args.putInt("user_id", userID)
//                    homeFragment.arguments = args
                    replaceFragment(jogosFragment)
                }
                R.id.menuAlugarQuadra -> {
                    val camposFragment = CampoGPSFragment()
//                    val args = Bundle()
//                    args.putInt("user_id", userID)
//                    homeFragment.arguments = args
                    replaceFragment(camposFragment)
                }
                R.id.menuPerfil -> {
                    val perfilFragment = PerfilLocadorFragment()
//                    val args = Bundle()
//                    args.putInt("user_id", userID)
//                    perfilFragment.arguments = args
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
        fragmentTransaction.replace(R.id.flMenuJogador, fragment)

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
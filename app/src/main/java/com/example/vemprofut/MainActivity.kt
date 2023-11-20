package com.example.vemprofut

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vemprofut.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val userID = intent.getIntExtra("user_id", -1)

        Toast.makeText(this, "Bem-Vindo, id:  $userID", Toast.LENGTH_LONG).show()


            val homeFragment = Home()
            val args = Bundle()
            args.putInt("user_id", userID)
            homeFragment.arguments = args
            replaceFragment(homeFragment)



        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.menuHome -> {

                        val homeFragment = Home()
                        val args = Bundle()
                        args.putInt("user_id", userID)
                        homeFragment.arguments = args
                        replaceFragment(homeFragment)

                }
                R.id.menuMeusJogos -> {

                    val jogosFragment = Jogos()
                    val args = Bundle()
                    args.putInt("user_id", userID)
                    homeFragment.arguments = args
                    replaceFragment(jogosFragment)
                }
                R.id.menuAlugarQuadra -> {
                    val camposFragment = CampoGPSFragment()
                    val args = Bundle()
                    args.putInt("user_id", userID)
                    homeFragment.arguments = args
                    replaceFragment(camposFragment)
                }
                R.id.menuPerfil -> {
                    val perfilFragment = Perfil()
                    val args = Bundle()
                    args.putInt("user_id", userID)
                    perfilFragment.arguments = args
                    replaceFragment(perfilFragment)
                }


                else ->{



                }

            }

            true

        }
    }

    // This method replaces the current fragment
    // with a new fragment
    fun replaceFragment(fragment: Fragment) {
        // Get a reference to the FragmentManager
        val fragmentManager = supportFragmentManager

        // Start a new FragmentTransaction
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Replace the current fragment with the new fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment)

        // Commit the FragmentTransaction
        fragmentTransaction.commit()
    }
}
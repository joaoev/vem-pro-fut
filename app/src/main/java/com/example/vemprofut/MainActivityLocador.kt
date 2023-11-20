package com.example.vemprofut

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.vemprofut.databinding.ActivityMainBinding
import com.example.vemprofut.databinding.ActivityMainLocadorBinding

class MainActivityLocador : AppCompatActivity() {
    private lateinit var binding : ActivityMainLocadorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainLocadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userID = intent.getIntExtra("user_id", -1)

        Toast.makeText(this, "Bem-Vindo, id:  $userID", Toast.LENGTH_LONG).show()

        val homeFragment = HomeLocadorFragment()
        val args = Bundle()
        args.putInt("user_id", userID)
        homeFragment.arguments = args
        replaceFragment(homeFragment)

        binding.bottomNavigationViewLocador.setOnItemSelectedListener {

            when(it.itemId){

                R.id.menuHomeLocador -> {
                    val homeFragment = HomeLocadorFragment()
                    val args = Bundle()
                    args.putInt("user_id", userID)
                    homeFragment.arguments = args
                    replaceFragment(homeFragment)
                }

                R.id.menuDashboardLocador-> {
                    val dashboardFragment = DashboardFragment()
                    val args = Bundle()
                    args.putInt("user_id", userID)
                    dashboardFragment.arguments = args
                    replaceFragment(dashboardFragment)
                }

                R.id.menuCamposLocador -> {
                    val camposFragment = CadastrarCampoFragment()
                    val args = Bundle()
                    args.putInt("user_id", userID)
                    camposFragment.arguments = args
                    replaceFragment(camposFragment)
                }

                R.id.menuTransacoesLocador -> {
                    val transacoesFragment = TransacoesFragment()
                    val args = Bundle()
                    args.putInt("user_id", userID)
                   transacoesFragment.arguments = args
                    replaceFragment(transacoesFragment)
                }

                R.id.menuPerfilLocador -> {
                    val perfilFragment = PerfilLocadorFragment()
                    val args = Bundle()
                    args.putInt("user_id", userID)
                    perfilFragment.arguments = args
                    replaceFragment(perfilFragment)
                }


                else -> {
                }
            }

            true

        }
    }
    override fun onBackPressed() {

        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 0) {
            // Se houver algo para voltar, execute o comportamento padrão (popBackStack)
            super.onBackPressed()
        } else {
            // Se não houver nada para voltar, exiba um diálogo de confirmação
            exibirDialogoDeConfirmacao()
        }
    }

    fun exibirDialogoDeConfirmacao() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmação")
            .setMessage("Deseja realmente fechar o aplicativo?")
            .setPositiveButton("Sim") { dialogInterface, _ ->
                // Se o usuário pressionar "Sim", feche o aplicativo
                dialogInterface.dismiss()
                finish()
            }
            .setNegativeButton("Não") { dialogInterface, _ ->
                // Se o usuário pressionar "Não", apenas feche o diálogo
                dialogInterface.dismiss()
            }
            .show()
    }




    // This method replaces the current fragment
    // with a new fragment
    fun replaceFragment(fragment: Fragment) {
        // Get a reference to the FragmentManager
        val fragmentManager = supportFragmentManager

        // Start a new FragmentTransaction
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Replace the current fragment with the new fragment
        fragmentTransaction.replace(R.id.frameLayoutLocador, fragment)

        // Commit the FragmentTransaction
        fragmentTransaction.commit()
    }
}
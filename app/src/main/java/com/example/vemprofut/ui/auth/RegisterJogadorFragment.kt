package com.example.vemprofut.ui.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentRegisterJogadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.example.vemprofut.model.Jogador
import com.example.vemprofut.model.Locador
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage


class RegisterJogadorFragment : Fragment() {

    private var _binding: FragmentRegisterJogadorBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var dialog: AlertDialog

    private lateinit var jogador: Jogador

    private var imageUri: Uri? = null

    companion object {
        private val PERMISSAO_GALERIA = Manifest.permission.READ_MEDIA_IMAGES
    }

    private val requestGaleria =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissao ->

            if (permissao) {
                resultGaleria.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            } else {
                showDialogPermissao()
            }
        }

    private val resultGaleria =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == Activity.RESULT_OK && result.data != null) {
                // A resposta está OK e há dados
                val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        result.data?.data
                    )
                } else {
                    val source = ImageDecoder.createSource(
                        requireContext().contentResolver,
                        result.data?.data!!
                    )
                    ImageDecoder.decodeBitmap(source)
                }

                binding.imgCadastroJogador.setImageBitmap(bitmap)
                imageUri = result.data?.data
            } else {
                Toast.makeText(requireContext(), "Nenhuma Imagem adicionada", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterJogadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClicks()
    }

    private fun initClicks() {
        binding.btnCadastroJogadorCriarConta.setOnClickListener(){
            validateData()
        }

        binding.btnCadastroJogadorImagem.setOnClickListener(){
            verificaPermissaoGaleria()
        }
    }

    private fun verificaPermissaoGaleria() {
        val permissaoGaleriaAceita = verificaPermissao(RegisterJogadorFragment.PERMISSAO_GALERIA)
        Toast.makeText(requireContext(), "${permissaoGaleriaAceita}!", Toast.LENGTH_SHORT).show()
        when {
            permissaoGaleriaAceita -> {
                resultGaleria.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            }

            shouldShowRequestPermissionRationale(RegisterJogadorFragment.PERMISSAO_GALERIA) -> showDialogPermissao()

            else -> requestGaleria.launch(RegisterJogadorFragment.PERMISSAO_GALERIA)
        }
    }

    private fun showDialogPermissao() {
        val buider = AlertDialog.Builder(requireContext())
            .setTitle("Atenção")
            .setMessage("Precisamos de acesso a galeria, permitir agora?")
            .setNegativeButton("Não") {_, _->
                dialog.dismiss()
            }
            .setPositiveButton("Sim") {_, _->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", requireContext().packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }

        dialog = buider.create()
        dialog.show()
    }

    private fun verificaPermissao(permissao: String) = ContextCompat.checkSelfPermission(requireContext(), permissao) == PackageManager.PERMISSION_GRANTED

    private fun validateData() {
        val nome = binding.edtCadastroJogadorNome.text.toString().trim()
        val nick = binding.edtCadastroJogadorNick.text.toString().trim()
        val email = binding.edtCadastroJogadorEmail.text.toString().trim()
        val senha = binding.edtCadastroJogadorSenha.text.toString().trim()

        if (nome.isNotEmpty() &&
            nick.isNotEmpty() &&
            email.isNotEmpty() &&
            senha.isNotEmpty()) {

            binding.progressBarJogador.isVisible = true

            registerJogador(nome, nick, email, senha, imageUri)
        } else {
            Toast.makeText(requireContext(), "Nenhum campo pode estar vazio!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerJogador(nome: String,
                                nick: String,
                                email: String,
                                senha: String,
                                imageUri: Uri?) {

        jogador = Jogador(
            fullname = nome,
            nickname = nick,
            email = email,
            password = senha,
        )



        val storage = FirebaseStorage.getInstance()


        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    jogador.id = FirebaseHelper.getIdUser() ?: ""

                    val storageRef = storage.reference.child("jogador/foto_perfil/foto_perfil_${jogador.id}.jpg")

                    if (imageUri != null) {
                        storageRef.putFile(imageUri)
                            .addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { imageUrlWeb ->
                                    jogador.urlImage = imageUrlWeb.toString()
                                    saveUserData(jogador)
                                }.addOnFailureListener { exception ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Erro ao obter a URL da imagem",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    saveUserData(jogador)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    requireContext(),
                                    "Erro ao salvar imagem",
                                    Toast.LENGTH_SHORT
                                ).show()

                                saveUserData(jogador)
                            }
                    }
                    saveUserData(jogador)
                } else {
                    Toast.makeText(requireContext(), task.exception?.message ?: "", Toast.LENGTH_SHORT).show()
                    binding.progressBarJogador.isVisible = false
                }
            }


    }

    private fun saveUserData(jogador: Jogador) {
        FirebaseHelper
            .getDatabase()
            .child("jogador")
            .child(jogador.id)
            .setValue(jogador)
            .addOnCompleteListener { jogador ->
                if (jogador.isSuccessful) {
                    findNavController().navigate(R.id.action_global_appJogadorFragment)
                } else {
                    Toast.makeText(requireContext(), "Erro: jogador.isSuccesful == false", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                binding.progressBarJogador.isVisible = false
                Toast.makeText(requireContext(),"Erro: addOnFailureListener", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
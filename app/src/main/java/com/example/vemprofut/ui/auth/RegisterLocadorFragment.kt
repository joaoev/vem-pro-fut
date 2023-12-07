package com.example.vemprofut.ui.auth

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.Manifest
import android.app.Activity
import android.util.Log
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
import com.example.vemprofut.databinding.FragmentRegisterLocadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.example.vemprofut.model.Locador
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.math.log

class RegisterLocadorFragment : Fragment() {
    private var _binding: FragmentRegisterLocadorBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var dialog: AlertDialog

    private lateinit var locador: Locador

    private var imageUri: Uri? = null

    companion object {
        private val PERMISSAO_GALERIA = Manifest.permission.READ_MEDIA_IMAGES
    }

    private val requestGaleria =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {permissao ->

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

                binding.imgCadastroLocador.setImageBitmap(bitmap)
                imageUri = result.data?.data
            } else {
                Toast.makeText(requireContext(), "Nenhuma Imagem adicionada", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterLocadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClicks()
    }

    private fun initClicks() {
        binding.buttonCadastroLocador.setOnClickListener(){
            validateData()
        }

        binding.btnCadastroLocadorImagem.setOnClickListener(){
            verificaPermissaoGaleria()
        }
    }

    private fun verificaPermissaoGaleria() {
        val permissaoGaleriaAceita = verificaPermissao(PERMISSAO_GALERIA)
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

            shouldShowRequestPermissionRationale(PERMISSAO_GALERIA) -> showDialogPermissao()

            else -> requestGaleria.launch(PERMISSAO_GALERIA)
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
        val nome = binding.editTextCadastroLocadorNome.text.toString().trim()
        val cpf = binding.editTextCadastroLocadorCPF.text.toString().trim()
        val nomeEmpresa = binding.editTextCadastroLocadorNomeEmpresa.text.toString().trim()
        val cnpj = binding.editTextCadastroLocadorCNPJ.text.toString().trim()
        val email = binding.editTextCadastroLocadorEmail.text.toString().trim()
        val telefone = binding.editTextCadastroLocadorTelefone.text.toString().trim()
        val senha = binding.editTextCadastroLocadorSenha.text.toString().trim()

        if (nome.isNotEmpty() &&
            cpf.isNotEmpty() &&
            nomeEmpresa.isNotEmpty() &&
            cnpj.isNotEmpty() &&
            email.isNotEmpty() &&
            telefone.isNotEmpty() &&
            senha.isNotEmpty()) {

            binding.progressBar.isVisible = true

            registerLocador(nome, cpf, nomeEmpresa, cnpj, email, telefone, senha, imageUri)

        } else {
            Toast.makeText(requireContext(), "Nenhum campo pode estar vazio!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerLocador(nome: String,
                                cpf: String,
                                nomeEmpresa: String,
                                cnpj: String,
                                email: String,
                                telefone: String,
                                senha: String,
                                imageUri: Uri?) {

        locador = Locador(
            fullname = nome,
            cpf = cpf,
            companyName = nomeEmpresa,
            cnpj = cnpj,
            email = email,
            phone = telefone,
            password = senha,
            )



        val storage = FirebaseStorage.getInstance()


        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    locador.id = FirebaseHelper.getIdUser() ?: ""

                    val storageRef = storage.reference.child("locador/foto_perfil/foto_perfil_${locador.id}.jpg")

                    if (imageUri != null) {
                        storageRef.putFile(imageUri)
                            .addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { imageUrlWeb ->
//                                    Log.d("IMAGE", "image imageUrlWeb: ${imageUrlWeb} ---- image imageUrlWeb.toStrig(): ${imageUrlWeb.toString()}")
                                    locador.urlImage = imageUrlWeb.toString()

//                                    Log.d("LOCADOR",    "locador.urlImage: ${locador.urlImage}")
                                    saveUserData(locador)
                                }.addOnFailureListener { exception ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Erro ao obter a URL da imagem",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    saveUserData(locador)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    requireContext(),
                                    "Erro ao salvar imagem",
                                    Toast.LENGTH_SHORT
                                ).show()

                                saveUserData(locador)
                            }
                    } else {
                        saveUserData(locador)
                    }

                } else {
                    Toast.makeText(requireContext(), task.exception?.message ?: "", Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible = false
                }
            }


    }

    private fun saveUserData(locador: Locador) {
        FirebaseHelper
            .getDatabase()
            .child("locador")
            .child(locador.id)
            .setValue(locador)
            .addOnCompleteListener { locador ->
                if (locador.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Conta criada",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().navigate(R.id.action_global_appLocadorFragment)
                } else {
                    Toast.makeText(requireContext(), "Erro ao criar 1", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(),"Erro ao criar 2", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.vemprofut.ui

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
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentEditarPerfilLocadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage


class EditarPerfilLocadorFragment : Fragment() {
    private var _binding: FragmentEditarPerfilLocadorBinding? = null
    private val binding get() = _binding!!


    private lateinit var dialog: AlertDialog

    private var imageUri: Uri? = null

    private lateinit var database: DatabaseReference

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

                binding.imgEditarPerfilLocador.setImageBitmap(bitmap)
                imageUri = result.data?.data

                atualizarImagem(imageUri)
            } else {
                Toast.makeText(requireContext(), "Nenhuma Imagem adicionada", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarPerfilLocadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        getInfosLocador()
        initClicks()

    }


    private fun initClicks() {
        binding.btnEditarPerfilLocadorSalvar.setOnClickListener(){
            validateData()
        }

        binding.btnEditarPerfilImagemGaleria.setOnClickListener(){
            verificaPermissaoGaleria()
        }

        binding.toobar.setNavigationOnClickListener() {
            val meuFragment = PerfilLocadorFragment()

            val fragmentManager = parentFragmentManager

            val transaction = fragmentManager.beginTransaction()

            transaction.replace(R.id.flAppLocador, meuFragment)

            transaction.commit()
        }
    }

    private fun verificaPermissaoGaleria() {
        val permissaoGaleriaAceita = verificaPermissao(EditarPerfilLocadorFragment.PERMISSAO_GALERIA)
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

            shouldShowRequestPermissionRationale(EditarPerfilLocadorFragment.PERMISSAO_GALERIA) -> showDialogPermissao()

            else -> requestGaleria.launch(EditarPerfilLocadorFragment.PERMISSAO_GALERIA)
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

    private fun getInfosLocador() {
        FirebaseHelper.getDatabase()
            .child("locador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if(isAdded) {
                        binding.edtEditarPerfilLocadorNome.setText(dataSnapshot.child("fullname").getValue(String::class.java))
                        binding.edtEditarPerfilLocadorCPF.setText( dataSnapshot.child("cpf").getValue(String::class.java))
                        binding.edtEditarPerfilLocadorTelefone.setText(dataSnapshot.child("phone").getValue(String::class.java))
                        binding.edtEditarPerfilLocadorNomeEmpresa.setText(dataSnapshot.child("companyName").getValue(String::class.java))
                        binding.edtEditarPerfilLocadorCNPJ.setText( dataSnapshot.child("cnpj").getValue(String::class.java))

                        binding.imgEditarPerfilLocador.load(dataSnapshot.child("urlImage").getValue(String::class.java)){
                            placeholder(R.drawable.add_image_bg)
                        } }


                    } else {
                        if (isAdded) {
                            binding.edtEditarPerfilLocadorNome.setText("Locador: Nenhuma Informação encontrada")
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), "FirebaseData, Erro ao obter dados do Firebase", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun validateData() {
        val nome = binding.edtEditarPerfilLocadorNome.text.toString().trim()
        val cpf = binding.edtEditarPerfilLocadorCPF.text.toString().trim()
        val nomeEmpresa = binding.edtEditarPerfilLocadorNomeEmpresa.text.toString().trim()
        val cnpj = binding.edtEditarPerfilLocadorCNPJ.text.toString().trim()
        val telefone = binding.edtEditarPerfilLocadorTelefone.text.toString().trim()

        if (nome.isNotEmpty() &&
            cpf.isNotEmpty() &&
            nomeEmpresa.isNotEmpty() &&
            cnpj.isNotEmpty() &&
            telefone.isNotEmpty()
            ) {

            binding.progressBarEditarPerfilLocador.isVisible = true

            updateUser(nome, cpf, nomeEmpresa, cnpj, telefone)

        } else {
            Toast.makeText(requireContext(), "Nenhum campo pode estar vazio!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUser(     nome: String,
                                cpf: String,
                                nomeEmpresa: String,
                                cnpj: String,
                                telefone: String,
                               ) {

        val camposParaAtualizar = mapOf(
            "fullname" to nome,
            "cpf" to cpf,
            "phone" to telefone,
            "companyName" to nomeEmpresa,
            "cnpj" to cnpj
        )

        atualizarCampos(camposParaAtualizar)

    }

    fun atualizarCampos(campos: Map<String, Any>) {
        FirebaseHelper.getDatabase()
            .child("locador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .updateChildren(campos)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),

                    "Locador: Sucesso ao atualizar dados!",
                    Toast.LENGTH_SHORT
                ).show()

                val meuFragment = PerfilLocadorFragment()

                val fragmentManager = parentFragmentManager

                val transaction = fragmentManager.beginTransaction()

                transaction.replace(R.id.flAppLocador, meuFragment)

                transaction.commit()

            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Locador: Falha ao atualizar dados!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun atualizarImagem(imageUri: Uri?) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("locador/foto_perfil/foto_perfil_${FirebaseHelper.getIdUser() ?: "erro"}.jpg")


        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                //deleta a imagem
                storageRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Locador: Sucesso, Imagem deletada!",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (imageUri != null) {
                            storageRef.putFile(imageUri)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { imageUrlWeb ->

                                        Toast.makeText(
                                            requireContext(),
                                            "Locador: Sucesso, upload da imagem",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val urlImagem = mapOf(
                                            "urlImage" to imageUrlWeb.toString()
                                        )

                                        atualizarUrlImagem(urlImagem)

                                    }.addOnFailureListener { exception ->
                                        Toast.makeText(
                                            requireContext(),
                                            "Locador: Erro ao obter a URL da imagem",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Locador: Erro ao fazer upload da imagem",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Locador: Falha, Imagem não deletada!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }
            .addOnFailureListener { exception ->
                if (imageUri != null) {
                    storageRef.putFile(imageUri)
                        .addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { imageUrlWeb ->

                                Toast.makeText(
                                    requireContext(),
                                    "Locador: Sucesso, upload da imagem",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val urlImagem = mapOf(
                                    "urlImage" to imageUrlWeb.toString()
                                )

                                atualizarUrlImagem(urlImagem)

                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    requireContext(),
                                    "Locador: Erro ao obter a URL da imagem",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                requireContext(),
                                "Locador: Erro ao fazer upload da imagem",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }


    }

    fun atualizarUrlImagem(campos: Map<String, Any>) {
        FirebaseHelper.getDatabase()
            .child("locador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .updateChildren(campos)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Locador: Imagem atualizada!",
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Locador: Falha ao atualizar imagem!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
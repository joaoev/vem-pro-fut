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
import com.example.vemprofut.databinding.FragmentEditarPerfilJogadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage

class EditarPerfilJogadorFragment : Fragment() {

    private var _binding: FragmentEditarPerfilJogadorBinding? = null
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

                binding.imgEditarPerfilJogador.setImageBitmap(bitmap)
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
        _binding = FragmentEditarPerfilJogadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        getInfosJogador()
        initClicks()
    }

    private fun initClicks() {
        binding.btnEditarPerfilJogadorSalvar.setOnClickListener(){
            validateData()
        }

        binding.btnEditarPerfilImagemGaleria.setOnClickListener(){
            verificaPermissaoGaleria()
        }

        binding.toobar.setNavigationOnClickListener() {
            val actions = EditarPerfilJogadorFragmentDirections.actionEditarPerfilJogadorFragmentToAppJogadorFragment()
            findNavController().navigate(actions)
        }
    }

    private fun verificaPermissaoGaleria() {
        val permissaoGaleriaAceita = verificaPermissao(EditarPerfilJogadorFragment.PERMISSAO_GALERIA)
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

            shouldShowRequestPermissionRationale(EditarPerfilJogadorFragment.PERMISSAO_GALERIA) -> showDialogPermissao()

            else -> requestGaleria.launch(EditarPerfilJogadorFragment.PERMISSAO_GALERIA)
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

    private fun getInfosJogador() {
        FirebaseHelper.getDatabase()
            .child("jogador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if(isAdded) {
                            binding.edtEditarPerfilJogadorNome.setText(dataSnapshot.child("fullname").getValue(String::class.java))
                            binding.edtEditarPerfilJogadorNick.setText( dataSnapshot.child("nickname").getValue(String::class.java))

                            binding.imgEditarPerfilJogador.load(dataSnapshot.child("urlImage").getValue(String::class.java)){
                                placeholder(R.drawable.add_image_bg)
                            }
                        }


                    } else {
                        if (isAdded) {
                            binding.edtEditarPerfilJogadorNome.setText("Locador: Nenhuma Informação encontrada")
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
        val nome = binding.edtEditarPerfilJogadorNome.text.toString().trim()
        val nick = binding.edtEditarPerfilJogadorNick.text.toString().trim()

        if (nome.isNotEmpty() && nick.isNotEmpty()) {
            binding.progressBarEditarPerfilJogador.isVisible = true

            updateUser(nome, nick)

        } else {
            Toast.makeText(requireContext(), "Nenhum campo pode estar vazio!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUser(nome: String, nick: String) {
        val camposParaAtualizar = mapOf(
            "fullname" to nome,
            "nickname" to nick,
        )

        atualizarCampos(camposParaAtualizar)
    }

    fun atualizarCampos(campos: Map<String, Any>) {
        FirebaseHelper.getDatabase()
            .child("jogador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .updateChildren(campos)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),

                    "Jogador: Sucesso ao atualizar dados!",
                    Toast.LENGTH_SHORT
                ).show()

                val actions = EditarPerfilJogadorFragmentDirections.actionEditarPerfilJogadorFragmentToAppJogadorFragment()
                findNavController().navigate(actions)

            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Jogador: Falha ao atualizar dados!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun atualizarImagem(imageUri: Uri?) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("jogador/foto_perfil/foto_perfil_${FirebaseHelper.getIdUser() ?: "erro"}.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                //deleta a imagem
                storageRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Jogador: Sucesso, Imagem deletada!",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (imageUri != null) {
                            storageRef.putFile(imageUri)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { imageUrlWeb ->


                                        val urlImagem = mapOf(
                                            "urlImage" to imageUrlWeb.toString()
                                        )

                                        atualizarUrlImagem(urlImagem)

                                    }.addOnFailureListener { exception ->

                                    }
                                }
                                .addOnFailureListener { exception ->

                                }
                        }

                    }.addOnFailureListener {

                    }

            }
            .addOnFailureListener { exception ->
                if (imageUri != null) {
                    storageRef.putFile(imageUri)
                        .addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { imageUrlWeb ->

                                Toast.makeText(
                                    requireContext(),
                                    "Jogador: Sucesso, upload da imagem",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val urlImagem = mapOf(
                                    "urlImage" to imageUrlWeb.toString()
                                )

                                atualizarUrlImagem(urlImagem)

                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    requireContext(),
                                    "Jogador: Erro ao obter a URL da imagem",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                requireContext(),
                                "Jogador: Erro ao fazer upload da imagem",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }



    }

    fun atualizarUrlImagem(campos: Map<String, Any>) {
        FirebaseHelper.getDatabase()
            .child("jogador")
            .child(FirebaseHelper.getIdUser() ?: "erro")
            .updateChildren(campos)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Jogador: Imagem atualizada!",
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Jogador: Falha ao atualizar imagem!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
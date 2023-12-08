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
import com.example.vemprofut.databinding.FragmentCadastrarCampoBinding
import com.example.vemprofut.databinding.FragmentEditarCampoBinding
import com.example.vemprofut.databinding.FragmentVerCampoLocadorBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.example.vemprofut.model.Campo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class EditarCampoFragment : Fragment() {
    private var _binding: FragmentEditarCampoBinding? = null
    private val binding get() = _binding!!
    private var campo_id: String? = null
    private lateinit var dialog: AlertDialog
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

                binding.imgEditarCampo.setImageBitmap(bitmap)
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
        _binding = FragmentEditarCampoBinding.inflate(inflater, container, false)
        campo_id = arguments?.getString("campo_id")?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInfosCampo()
        initClicks()
    }

    private fun initClicks() {
        binding.btnEditarCampo.setOnClickListener(){
            validateData()
        }

        binding.btnEditarCampoImagem.setOnClickListener(){
            verificaPermissaoGaleria()
        }

        binding.toobarEditarCampoLocador.setOnClickListener(){
            val meuFragment = VerCampoLocadorFragment()

            val args = Bundle()
            args.putString("campo_id", campo_id)
            meuFragment.arguments = args

            val fragmentManager = parentFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.flAppLocador, meuFragment)
            transaction.commit()
        }
    }

    private fun verificaPermissaoGaleria() {
        val permissaoGaleriaAceita = verificaPermissao(EditarCampoFragment.PERMISSAO_GALERIA)
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

            shouldShowRequestPermissionRationale(EditarCampoFragment.PERMISSAO_GALERIA) -> showDialogPermissao()

            else -> requestGaleria.launch(EditarCampoFragment.PERMISSAO_GALERIA)
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
        val nome = binding.edtEditarCampoNome.text.toString().trim()
        val descricao = binding.edtEditarCampoDescricao.text.toString().trim()
        val valorhora = binding.edtEditarCampoValorHora.text.toString().trim()

        val cbEst = binding.cbEditarCampoEst.isChecked
        val cbVes = binding.cbEditarCampoVes.isChecked
        val cbBar = binding.cbEditarCampoBar.isChecked

        val horaInicio = binding.edtEditarCampoHoraInicio.text.toString().trim()
        val horaFim = binding.edtEditarCampoHoraFim.text.toString().trim()

        val cbSeg = binding.cbSegHorario.isChecked
        val cbTer = binding.cbTerHorario.isChecked
        val cbQua = binding.cbQuaHorario.isChecked
        val cbQui = binding.cbQuiHorario.isChecked
        val cbSex = binding.cbSexHorario.isChecked
        val cbSab = binding.cbSabHorario.isChecked
        val cbDom = binding.cbDomHorario.isChecked

        val listaComodidades = ArrayList<Boolean>()
        val listaDiasDaSemana = ArrayList<Boolean>()

        listaComodidades.add(cbEst)
        listaComodidades.add(cbVes)
        listaComodidades.add(cbBar)

        listaDiasDaSemana.add(cbSeg)
        listaDiasDaSemana.add(cbTer)
        listaDiasDaSemana.add(cbQua)
        listaDiasDaSemana.add(cbQui)
        listaDiasDaSemana.add(cbSex)
        listaDiasDaSemana.add(cbSab)
        listaDiasDaSemana.add(cbDom)

        binding.progressBarEditarCampo.isVisible = true

        if (nome.isNotEmpty() && descricao.isNotEmpty() && valorhora.isNotEmpty() && horaInicio.isNotEmpty() && horaFim.isNotEmpty()) {
            val horaInicio = horaInicio.toInt()
            val horaFim = horaFim.toInt()
            val valorHoraDouble = valorhora.toDouble()

            var diasSelecionados = 0;

            for (valor in listaDiasDaSemana) {
                if (valor) {
                    diasSelecionados++
                }
            }

            if (diasSelecionados != 0) {

                if (horaInicio in 0..23 && horaFim in 0..23) {
                    if (!(horaInicio > horaFim) && horaInicio != horaFim) {
                        if (valorHoraDouble >= 0) {
                            updateCampo(
                                nome,
                                descricao,
                                valorHoraDouble,
                                horaInicio,
                                horaFim,
                                listaComodidades,
                                listaDiasDaSemana,
                            )
                        } else {
                            Toast.makeText(requireContext(), "Erro: Valor da Hora, negativo", Toast.LENGTH_SHORT).show()
                            binding.progressBarEditarCampo.isVisible = false
                        }
                    } else {
                        Toast.makeText(requireContext(), "Erro: intervalo inválido", Toast.LENGTH_SHORT).show()
                        binding.progressBarEditarCampo.isVisible = false
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro: Valor de inicio ou fim fora do intervalo", Toast.LENGTH_SHORT).show()
                    binding.progressBarEditarCampo.isVisible = false
                }

            } else {
                Toast.makeText(requireContext(), "Selecione pelo menos algum dia!", Toast.LENGTH_SHORT).show()
                binding.progressBarEditarCampo.isVisible = false
            }

        } else {
            Toast.makeText(requireContext(), "Nenhum campo pode estar vazio!", Toast.LENGTH_SHORT).show()
            binding.progressBarEditarCampo.isVisible = false
        }
    }

    private fun updateCampo(nome: String, descricao: String, valorHora: Double, horaInicio: Int, horaFim: Int, listaComodidades: ArrayList<Boolean>, listaDiasDaSemana: ArrayList<Boolean>) {
        val camposParaAtualizar = mapOf(
            "local_name" to nome,
            "description" to descricao,
            "hourly_rate" to valorHora,
            "time_start" to horaInicio,
            "time_end" to horaFim,
            "parking" to listaComodidades.get(0),
            "locker_room" to listaComodidades.get(1),
            "pub" to listaComodidades.get(2),
            "seg" to listaDiasDaSemana.get(0),
            "ter" to listaDiasDaSemana.get(1),
            "qua" to listaDiasDaSemana.get(2),
            "qui" to listaDiasDaSemana.get(3),
            "sex" to listaDiasDaSemana.get(4),
            "sab" to listaDiasDaSemana.get(5),
            "dom" to listaDiasDaSemana.get(6),
        )

        atualizarCampos(camposParaAtualizar)
    }

    fun atualizarCampos(campos: Map<String, Any>) {
        FirebaseHelper.getDatabase()
            .child("campos")
            .child(campo_id ?: "null")
            .updateChildren(campos)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),

                    "Campo: Sucesso ao atualizar dados!",
                    Toast.LENGTH_SHORT
                ).show()

                val meuFragment = HomeLocadorFragment()
                val fragmentManager = parentFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.flAppLocador, meuFragment)
                transaction.commit()

            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Campo: Falha ao atualizar dados!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun getInfosCampo() {
        FirebaseHelper.getDatabase()
            .child("campos")
            .child(campo_id?:"null")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (isAdded) {
                            binding.edtEditarCampoNome.setText(dataSnapshot.child("local_name").getValue(String::class.java))
                            binding.edtEditarCampoDescricao.setText(dataSnapshot.child("description").getValue(String::class.java))
                            binding.edtEditarCampoValorHora.setText(dataSnapshot.child("hourly_rate").getValue(Double::class.java).toString())

                            binding.cbEditarCampoVes.isChecked = dataSnapshot.child("locker_room").getValue(Boolean::class.java)?:false
                            binding.cbEditarCampoEst.isChecked =  dataSnapshot.child("parking").getValue(Boolean::class.java)?:false
                            binding.cbEditarCampoBar.isChecked = dataSnapshot.child("pub").getValue(Boolean::class.java)?:false
                            
                            binding.edtEditarCampoHoraInicio.setText(dataSnapshot.child("time_start").getValue(Int::class.java).toString())
                            binding.edtEditarCampoHoraFim.setText(dataSnapshot.child("time_end").getValue(Int::class.java).toString())

                            binding.cbSegHorario.isChecked = (dataSnapshot.child("seg").getValue(Boolean::class.java) ?: false)
                            binding.cbTerHorario.isChecked = (dataSnapshot.child("ter").getValue(Boolean::class.java) ?: false)
                            binding.cbQuaHorario.isChecked = (dataSnapshot.child("qua").getValue(Boolean::class.java) ?: false)
                            binding.cbQuiHorario.isChecked = (dataSnapshot.child("qui").getValue(Boolean::class.java) ?: false)
                            binding.cbSexHorario.isChecked = (dataSnapshot.child("sex").getValue(Boolean::class.java) ?: false)
                            binding.cbSabHorario.isChecked = (dataSnapshot.child("sab").getValue(Boolean::class.java) ?: false)
                            binding.cbDomHorario.isChecked = (dataSnapshot.child("dom").getValue(Boolean::class.java) ?: false)

                            binding.imgEditarCampo.load(dataSnapshot.child("url_image").getValue(String::class.java)){
                                placeholder(R.drawable.add_image_bg_square)
                                error(R.drawable.add_image_bg_square)
                            }
                        }

                    } else {
                        if (isAdded) {
                            binding.edtEditarCampoNome.setText("Erro ao obter informações.")
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    fun atualizarImagem(imageUri: Uri?) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("campos/imagens_campos${campo_id ?: "erro"}.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                //deleta a imagem
                storageRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Campo: Sucesso, Imagem deletada!",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (imageUri != null) {
                            storageRef.putFile(imageUri)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { imageUrlWeb ->

                                        Toast.makeText(
                                            requireContext(),
                                            "Campo: Sucesso, upload da imagem",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val urlImagem = mapOf(
                                            "url_image" to imageUrlWeb.toString()
                                        )

                                        atualizarUrlImagem(urlImagem)

                                    }.addOnFailureListener { exception ->
                                        Toast.makeText(
                                            requireContext(),
                                            "Campo: Erro ao obter a URL da imagem",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Campo: Erro ao fazer upload da imagem",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Campo: Falha, Imagem não deletada!",
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
                                    "Campo: Sucesso, upload da imagem",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val urlImagem = mapOf(
                                    "url_image" to imageUrlWeb.toString()
                                )

                                atualizarUrlImagem(urlImagem)

                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    requireContext(),
                                    "Campo: Erro ao obter a URL da imagem",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                requireContext(),
                                "Campo: Erro ao fazer upload da imagem",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }



    }

    fun atualizarUrlImagem(campos: Map<String, Any>) {
        FirebaseHelper.getDatabase()
            .child("campos")
            .child(campo_id ?: "erro")
            .updateChildren(campos)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Campo: Imagem atualizada!",
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Campo: Falha ao atualizar imagem!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
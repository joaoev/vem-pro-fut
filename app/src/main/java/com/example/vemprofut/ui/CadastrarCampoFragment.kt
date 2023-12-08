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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.databinding.FragmentCadastrarCampoBinding
import com.example.vemprofut.helper.FirebaseHelper
import com.example.vemprofut.model.Campo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class CadastrarCampoFragment : Fragment() {
    private var _binding: FragmentCadastrarCampoBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: AlertDialog

    private lateinit var campo: Campo
    private var endereco : String = ""

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

                binding.imgCadastroCampo.setImageBitmap(bitmap)
                imageUri = result.data?.data
            } else {
                Toast.makeText(requireContext(), "Nenhuma Imagem adicionada", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCadastrarCampoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
    }

    private fun initClicks() {
        binding.btnCadastrarCampo.setOnClickListener(){
            validateData()
        }

        binding.btnCadastroCampoImagem.setOnClickListener(){
            verificaPermissaoGaleria()
        }
    }

    private fun verificaPermissaoGaleria() {
        val permissaoGaleriaAceita = verificaPermissao(CadastrarCampoFragment.PERMISSAO_GALERIA)
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

            shouldShowRequestPermissionRationale(CadastrarCampoFragment.PERMISSAO_GALERIA) -> showDialogPermissao()

            else -> requestGaleria.launch(CadastrarCampoFragment.PERMISSAO_GALERIA)
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
        val nome = binding.edtCadastrarCampoNome.text.toString().trim()
        val descricao = binding.edtCadastrarCampoDescricao.text.toString().trim()
        val valorhora = binding.edtCadastrarCampoValorHora.text.toString().trim()

        val cbEst = binding.cbCadastrarCampoEst.isChecked
        val cbVes = binding.cbCadastrarCampoVes.isChecked
        val cbBar = binding.cbCadastrarCampoBar.isChecked

        val horaInicio = binding.edtCadastroCampoHoraInicio.text.toString().trim()
        val horaFim = binding.edtCadastroCampoHoraFim.text.toString().trim()

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

        binding.progressBarCadastrarCampo.isVisible = true

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
                            registerCampo(
                                nome,
                                descricao,
                                valorHoraDouble,
                                horaInicio,
                                horaFim,
                                listaComodidades,
                                listaDiasDaSemana,
                                imageUri
                            )
                        } else {
                            Toast.makeText(requireContext(), "Erro: Valor da Hora, negativo", Toast.LENGTH_SHORT).show()
                            binding.progressBarCadastrarCampo.isVisible = false
                        }
                    } else {
                        Toast.makeText(requireContext(), "Erro: intervalo inválido", Toast.LENGTH_SHORT).show()
                        binding.progressBarCadastrarCampo.isVisible = false
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro: Valor de inicio ou fim fora do intervalo", Toast.LENGTH_SHORT).show()
                    binding.progressBarCadastrarCampo.isVisible = false
                }

            } else {
                Toast.makeText(requireContext(), "Selecione pelo menos algum dia!", Toast.LENGTH_SHORT).show()
                binding.progressBarCadastrarCampo.isVisible = false
            }

        } else {
            Toast.makeText(requireContext(), "Nenhum campo pode estar vazio!", Toast.LENGTH_SHORT).show()
            binding.progressBarCadastrarCampo.isVisible = false
        }
    }
    private fun registerCampo(nome: String,
                                descricao: String,
                                valorHora: Double,
                                horaInicio: Int,
                                horaFim: Int,
                                listaComodidades: ArrayList<Boolean>,
                                listaDiasDaSemana:ArrayList<Boolean>,
                                imageUri: Uri?) {


        FirebaseHelper.getDatabase()
            .child("locador")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                            endereco = dataSnapshot.child("address").getValue(String::class.java).toString()
                        campo = Campo(
                            id_locador = FirebaseHelper.getIdUser() ?: "",
                            local_name = nome,
                            description = descricao,
                            hourly_rate = valorHora,
                            parking = listaComodidades.get(0),
                            locker_room = listaComodidades.get(1),
                            pub = listaComodidades.get(2),
                            time_start = horaInicio,
                            time_end = horaFim,
                            seg = listaDiasDaSemana.get(0),
                            ter = listaDiasDaSemana.get(1),
                            qua = listaDiasDaSemana.get(2),
                            qui = listaDiasDaSemana.get(3),
                            sex = listaDiasDaSemana.get(4),
                            sab = listaDiasDaSemana.get(5),
                            dom = listaDiasDaSemana.get(6),
                            address = endereco
                        )


                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference.child("campos/imagens_campos/img_campo_${campo.id}.jpg")

                        if (imageUri != null) {
                            storageRef.putFile(imageUri)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { imageUrlWeb ->
                                        campo.url_image = imageUrlWeb.toString()
                                        saveCampoData(campo)
                                    }.addOnFailureListener { exception ->
                                        Toast.makeText(
                                            requireContext(), "Erro ao obter a URL da imagem", Toast.LENGTH_SHORT).show()
                                        saveCampoData(campo)
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Erro ao salvar imagem",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    saveCampoData(campo)
                                }
                        } else {
                            saveCampoData(campo)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })


    }

    private fun saveCampoData(campo: Campo) {
        FirebaseHelper
            .getDatabase()
            .child("campos")
            .child(campo.id)
            .setValue(campo)
            .addOnCompleteListener { campo ->
                if (campo.isSuccessful) {
                    binding.progressBarCadastrarCampo.isVisible = false

                    val fragmentManager = parentFragmentManager

                    val transaction = fragmentManager.beginTransaction()

                    transaction.replace(R.id.flAppLocador, CadastrarCampoFragment())

                    transaction.commit()

                } else {
                    Toast.makeText(requireContext(), "Erro ao criar 1", Toast.LENGTH_SHORT).show()
                    binding.progressBarCadastrarCampo.isVisible = false
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Erro ao criar 2", Toast.LENGTH_SHORT).show()
                binding.progressBarCadastrarCampo.isVisible = false
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


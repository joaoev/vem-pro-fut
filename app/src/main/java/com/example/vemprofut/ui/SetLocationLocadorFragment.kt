package com.example.vemprofut.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.LinearGradient
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.helper.FirebaseHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.IOException
import java.util.Locale

class SetLocationLocadorFragment : Fragment() {
    private var longitude = 2.0
    private var latitude = 2.0
    private var address_locador = ""
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var marker: Marker? = null

    private val callback = OnMapReadyCallback { googleMap ->
        if(isAdded) {
            fetchLocation {
                Log.d("LOX", "${latitude} ${longitude}")

                val loc = LatLng(latitude, longitude)
                val marker = googleMap.addMarker(
                    MarkerOptions().position(loc).title("Sua localização").draggable(true)
                )

                // Define o listener de arrastar no marcador
                marker?.let {
                    it.tag = "original" // Marcador original, para distinção
                    googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                        override fun onMarkerDragStart(p0: Marker) {
                        }

                        override fun onMarkerDrag(p0: Marker) {
                        }

                        override fun onMarkerDragEnd(marker: Marker) {

                            getAddressFromLocation(
                                requireContext(),
                                latitude,
                                longitude
                            ) { address ->
                                address_locador = address
                                marker.title = address
                                marker.showInfoWindow()
                                Log.d("LOX", "Novo endereço: $address")
                            }

                            // Chamado quando o usuário termina de arrastar o marcador
                            if (marker.tag == "original") {
                                latitude = marker.position.latitude
                                longitude = marker.position.longitude
                                Log.d("LOX", "Novas coordenadas: $latitude $longitude")

                                val btnLocadorGeo: Button = view!!.findViewById(R.id.btnLocadorGeo)

                                btnLocadorGeo.setOnClickListener() {
                                    updateUserInfo(latitude, longitude, address_locador)
                                }
                            }
                        }
                    })
                }

                // Define a posição e o nível de zoom desejados
                val cameraPosition = CameraPosition.Builder()
                    .target(loc) // Posição do marcador
                    .zoom(15f) // Nível de zoom desejado
                    .build()

                // Move a câmera para a posição e nível de zoom desejados
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                if (isAdded) {
                    getAddressFromLocation(requireContext(), latitude, longitude) { address ->
                        address_locador = address
                        marker?.title = address
                        marker?.showInfoWindow()
                        Log.d("LOX", "Novo endereço: $address")
                    }
                }

                val btnLocadorGeo: Button? = view?.findViewById(R.id.btnLocadorGeo)
                btnLocadorGeo?.setOnClickListener() {
                    updateUserInfo(latitude, longitude, address_locador)
                }
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return inflater.inflate(R.layout.fragment_set_location_locador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnVoltar: Toolbar? = view?.findViewById(R.id.toobarLoc)
        btnVoltar?.setOnClickListener() {
            val fragmentManager = parentFragmentManager

            val transaction = fragmentManager.beginTransaction()

            transaction.replace(R.id.flAppLocador, PerfilLocadorFragment())

            transaction.commit()
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapLocador) as SupportMapFragment?

        mapFragment?.getMapAsync(callback)
    }

    private fun fetchLocation(callback: () -> Unit) {
        if (!isLocationAvailable()) {
            Toast.makeText(
                requireContext(),
                "A localização não está disponível. Ative a localização e tente novamente.",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        val task: Task<Location> = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        task.addOnSuccessListener { location ->
            FirebaseHelper.getDatabase()
                .child("locador")
                .child(FirebaseHelper.getIdUser() ?: "erro")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            latitude =  dataSnapshot.child("latitude").getValue(Double::class.java) ?: 90.0
                            longitude = dataSnapshot.child("longitude").getValue(Double::class.java) ?: 180.0

                            Log.d("TESTELOC", "${latitude} ${longitude}")
                            if (latitude != 1.0 && longitude != 1.0) {
                                if (location != null) {
                                    latitude = latitude
                                    longitude = longitude
                                    if (isAdded) {
                                        callback.invoke()
                                    }

                                } else {
                                    Toast.makeText(requireContext(), "Erro ao obter a localização. Tente novamente.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                if (location != null) {
                                    latitude = location.latitude
                                    longitude = location.longitude
                                    callback.invoke() // Chama o callback após obter a localização
                                } else {
                                    Toast.makeText(requireContext(), "Erro ao obter a localização. Tente novamente.", Toast.LENGTH_LONG).show()
                                }
                            }

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(requireContext(), "FirebaseData, Erro ao obter dados do Firebase", Toast.LENGTH_SHORT)
                            .show()
                    }
                })


        }

        task.addOnFailureListener { exception ->
            Log.e("LocationError", "Erro ao obter a localização: ${exception.message}", exception)
            Toast.makeText(requireContext(), "Erro ao obter a localização. Tente novamente.", Toast.LENGTH_LONG).show()
        }
    }

    private fun isLocationAvailable(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun updateUserInfo(     latitude: Double,
                                longitude: Double,
                                    address: String
    ) {

        val camposParaAtualizar = mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "address" to address
        )

        atualizarCampos(camposParaAtualizar)

    }

    private fun getAddressFromLocation(
        context: Context,
        latitude: Double,
        longitude: Double,
        addressCallback: (String) -> Unit
    ) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: String = addresses[0].getAddressLine(0)
                addressCallback.invoke(address)
            } else {
                addressCallback.invoke("Endereço não disponível")
            }
        } catch (e: IOException) {
            Log.e("GeocodingError", "Erro ao obter endereço: ${e.message}", e)
            addressCallback.invoke("Erro ao obter endereço")
        }
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


            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Locador: Falha ao atualizar dados!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}
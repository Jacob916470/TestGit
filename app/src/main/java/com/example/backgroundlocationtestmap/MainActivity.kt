package com.example.backgroundlocationtestmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.backgroundlocationtestmap.MapUtils.setuMarkerData
import com.example.backgroundlocationtestmap.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding

    private lateinit var userRef: DatabaseReference

    private val user = FirebaseAuth.getInstance().currentUser

    lateinit var map: GoogleMap

    //Recolectamos posiciones del paseador
    private var locations = mutableListOf<LatLng>()

    val lat = 0.0
    val long = 0.0


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var locationCallback = object : LocationCallback() {
        /** No nos detecte "Ctrl i", asi que tecleamos "Ctrl o" para sobreescribir el metodo "onLocationResult" */
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            /** Lo que se hara aqui es que haremo sun recorrido con nuestro arreglo "locations", el cual
             * lo mandamos a llamar dentro de nuestro "forEach()", el que esta antes es propiedad de la variable "result" */
            result.locations.run {
                this.forEach {
                    val que: DatabaseReference = FirebaseDatabase.getInstance().reference.child("location").child(userRef.key.toString())
                    val q : Query= que.orderByKey().limitToLast(1)
                    q.addChildEventListener(object : ChildEventListener{
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            locations.add(
                                LatLng(
                                    LocationService().getLat(it.latitude),
                                    LocationService().getLong(it.longitude)
                                )
                            )
                            /** Para que esto funcione vamos a habilitar la ubicación del usuario, para esto la habilitaremos
                             * en "onMapReady", sera solo temporal */
                            Log.i("Fused location provider", "onLocationResult: $locations")
                            MapUtils.addPolyLine(map,locations)
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupPermissons()
        setupButtons()
        setupFirebase()
        setuMarkerData(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.requestLocationUpdates(
            MapUtils.locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )


    }


    private fun setupFirebase() {
        userRef = FirebaseDatabase.getInstance().reference.child(LocationApp.PATH_LOCATION)
    }


    private fun setupButtons() {

        binding.btnIniciar.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)

            }
        }

        binding.btnTerminar.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
    }

    private fun setupPermissons() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        MapUtils.setupHomeMap(this, map)
    }

}
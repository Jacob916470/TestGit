package com.example.backgroundlocationtestmap

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("DEPRECATION")
class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    private lateinit var userRef: DatabaseReference

    private var lat= 0.0
    private var long= 0.0

    lateinit var map: GoogleMap
    var lastLat: Double = 0.0
    var lastLong: Double = 0.0



    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        setupFirebase()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setupFirebase() {
        userRef = FirebaseDatabase.getInstance().reference.child(LocationApp.PATH_LOCATION)
    }




    private fun start() {

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                 lat = location.latitude.toString().takeLast(3).toDouble()
                 long = location.longitude.toString().takeLast(3).toDouble()

                getLat(lat)
                getLong(long)
                userRef.push().key!!.apply {
                save(this,lat.toString(),long.toString())
                }
                val updatedNotification = notification.setContentText(
                    "Location: ($lat, $long)"
                )
                notificationManager.notify(1,updatedNotification.build())

            }.launchIn(serviceScope)

        startForeground(1, notification.build())
    }


    private fun save(key: String,latitud: String, longitud: String){
        val locat = Location (latitud = latitud, longitud = longitud)
        userRef.child(key).setValue(locat)
            .addOnCanceledListener {
                applicationContext.let {
                    Toast.makeText(it,"sdgsd",Toast.LENGTH_LONG).show()
                }
            }
    }




    private fun stop() {
        stopForeground(true)
        stopSelf()
    }
    fun getLat(lat: Double) = lat
    fun getLong(long: Double) = long

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
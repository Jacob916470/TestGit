package com.example.backgroundlocationtestmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

object MapUtils {

    private lateinit var locationClient: LocationClient
    var lat: Double = 0.0
    var long: Double = 0.0

    private var walkerMarker: Marker? = null

    private var iconUserMarker: Bitmap? = null
    private var iconWalkerMarker: Bitmap? = null

    val locationRequest = LocationRequest.create()
        .setInterval(5_000)
        .setFastestInterval(2_000)


    fun setupHomeMap(context: Context, map: GoogleMap) {
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom((
                //LatLng(LocationService().getLat(),LocationService().getLong())), 18.0f))
        }

    fun setuMarkerData(context: Context){

        Utils.getbimapFromVector(context, R.drawable.ic_pin_casa)?.let {
            iconUserMarker = it
        }
    }

    /** Limpia nuestro marker para que no se sobreescriba */
    private fun removeOldDeliveryMarker()  = walkerMarker?.remove()

    fun addUserMarker(context: Context, map: GoogleMap, location: LatLng){
        iconUserMarker?.let {
            map.addMarker(
                MarkerOptions()
                    .position(location)
                    .anchor(0.3f,1f)
                    .icon(BitmapDescriptorFactory.fromBitmap(it))
                    //.title(formatTitle(context))
                    //.snippet(formatSnippet(context))
            )
        }
    }
    fun addPolyLine(map: GoogleMap, locations: MutableList<LatLng>) {
        val route = map.addPolyline(
            PolylineOptions()
                .width(16f)
                .color(Color.LTGRAY)
                .jointType(JointType.ROUND)
                .startCap(RoundCap())
                .addAll(locations)
        )
    }

}
package com.example.backgroundlocationtestmap

data class Tracking(
    private var latitud: Double = 0.0, private var longitud: Double=0.0){

    fun tracking(latitud: Double, longitud: Double) {
        this.latitud = latitud
        this.longitud = longitud
    }

    fun getLatitud(): Double {
        return latitud
    }

    fun getLongitud(): Double {
        return longitud
    }

}

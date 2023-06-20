package com.example.backgroundlocationtestmap

import com.google.firebase.database.Exclude


data class Location(@get:Exclude
    var id: String ="",
    var latitud :String,
    var longitud : String
)

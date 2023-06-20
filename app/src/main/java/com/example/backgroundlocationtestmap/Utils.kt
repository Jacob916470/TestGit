package com.example.backgroundlocationtestmap

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.viewbinding.ViewBinding
import com.google.android.gms.maps.GoogleMap

object Utils {

     var maps: GoogleMap = GoogleMap()



    private fun GoogleMap(): GoogleMap {
        return maps
    }

    /** Se necesitara un contexto y des pues un "id" de un "recurso" y a su vez la función retornara un
     * dato de tipo "Bitmap" que podria ser nulo */
    fun getbimapFromVector(context: Context, resId: Int): Bitmap? {
        /** Retornamos "AppCompatResources.getDrawable(context,resId)?.toBitmap()" */
        return AppCompatResources.getDrawable(context, resId)?.toBitmap()
    }

}
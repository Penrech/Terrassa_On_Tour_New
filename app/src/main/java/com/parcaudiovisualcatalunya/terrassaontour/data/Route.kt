package com.parcaudiovisualcatalunya.terrassaontour.data

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject

@Entity(tableName = "route_table")
class Route(@PrimaryKey var id: String,
            var title: String,
            var color: Int,
            var caracteristicas: ArrayList<String>,
            var puntos: ArrayList<PointLocation>,
            var idAudiovisuales: ArrayList<String>) {

    fun getPointsInLatLng(): ArrayList<LatLng>{
        val result = ArrayList<LatLng>()
        puntos.forEach { pointLocation ->
            result.add(LatLng(pointLocation.lat,pointLocation.lon))
        }

        return result
    }

    class PointLocation(var lat: Double, var lon: Double)

    companion object {
        fun jsonAReferencia(referencia: JSONObject) : Route? {
            try {

                val caracteristicas = referencia.getJSONObject("caracteristicas")
                val puntos = referencia.getJSONArray("puntos")
                val audiovisualesArray = referencia.getJSONArray("id_audiovisuales")

                val pointsToAdd = ArrayList<PointLocation>()
                val audToAdd = ArrayList<String>()
                val features = ArrayList<String>()

                for (i: Int in 0 until puntos.length()) {
                    val puntoInfo = puntos.getJSONObject(i)
                    val punto = PointLocation( puntoInfo.getString("lat").toDouble(), puntoInfo.getString("lon").toDouble())
                    pointsToAdd.add(punto)
                }

                for (i: Int in 0 until audiovisualesArray.length()){
                    val idAudiovisual = audiovisualesArray.getString(i)
                    audToAdd.add(idAudiovisual)
                }

                if (caracteristicas["guiada"] == true) features.add("Guiada")
                if (caracteristicas["exterior"] == true) features.add("Exterior")
                if (caracteristicas["interior"] == true) features.add("Interior")


                return Route(referencia.getString("id_ruta"),referencia.getString("title"),
                    Color.parseColor(referencia.getString("color")),
                    features,
                    pointsToAdd,
                    audToAdd)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}
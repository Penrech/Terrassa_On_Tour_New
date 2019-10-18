package com.parcaudiovisualcatalunya.terrassaontour.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONException
import org.json.JSONObject

@Entity (tableName = "point_table")
class POI(@PrimaryKey var id: String,
          var idTargetDia: String,
          var idTargetNoche: String,
          var title: String,
          var latitud: Double,
          var longitud: Double,
          var imgUrlSmall: String,
          var imgUrlSmallSecundary: String,
          var imgUrlBig: String,
          var imgUrlBigSecundary: String,
          var exterior: Boolean,
          var deDia: Boolean) {

    companion object {
        fun jsonAReferencia(referencia: JSONObject) : POI? {
            try {

                return POI(referencia.getString("id"),
                    referencia.getString("id_target_dia"),
                    referencia.getString("id_target_noche"),
                    referencia.getString("title"),
                    referencia.getString("lat").toDouble(),
                    referencia.getString("lon").toDouble(),
                    referencia.getString("img_url_small"),
                    referencia.getString("img_url_small_secundary"),
                    referencia.getString("img_url_big"),
                    referencia.getString("img_url_big_secundary"),
                    referencia.getString("exterior") == "1",
                    referencia.getBoolean("dia")
                    )

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}
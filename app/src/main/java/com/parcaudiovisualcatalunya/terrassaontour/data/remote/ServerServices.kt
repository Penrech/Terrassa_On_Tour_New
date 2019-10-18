package com.parcaudiovisualcatalunya.terrassaontour.data.remote

import android.content.Context
import android.util.Log
import com.parcaudiovisualcatalunya.terrassaontour.R
import com.parcaudiovisualcatalunya.terrassaontour.data.Audiovisual
import com.parcaudiovisualcatalunya.terrassaontour.data.POI
import com.parcaudiovisualcatalunya.terrassaontour.data.Route
import com.parcaudiovisualcatalunya.terrassaontour.data.Statics
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL

class ServerServices(context: Context) {

    //todo extraer todo en recursos

    private val baseURL = context.getString(R.string.server_main_address)

    private val poiURL = "${baseURL}getMarkers.php"
    private val rutesURL = "${baseURL}getRoutes.php"
    private val audiovisualsURL = "${baseURL}getAllAudiovisuals.php"
    private val insertUser = "${baseURL}insertUser.php"
    private val insertAllStatics = "${baseURL}InsertAllStatistics.php"

    fun insertUserIfNeeded(userID: String, userDeviceMode: String, userDeviceName: String, userDeviceType: String): Pair<Boolean,Long?>{
        val url =
            "$insertUser?id=$userID&model=$userDeviceMode&name=$userDeviceName&type=$userDeviceType"
        try {
            val json = JSONObject(getJson(url))
            val success = json.getString("result") == "success"
            Log.i("Data","El resultado de success es: $success")
            val lastUpdate = json.getLong("lastUpdate")
            return Pair(success,lastUpdate)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false,null)
        }
    }

    fun insertPeriodicallyStatics(postData: JSONObject): Statics.InsertStaticsResponse {
        val response = Statics.InsertStaticsResponse()

        try {
            val json = JSONObject(getJsonPOST(insertAllStatics,postData))
            val appState = json.getJSONObject("appState")
            val audiovisuals = json.getJSONObject("audiovisuals")
            val successAudiovisuals = audiovisuals.getJSONArray("success")
            val points = json.getJSONObject("points")
            val successPoints = points.getJSONArray("success")
            val rutes = json.getJSONObject("rutes")
            val successRutes = rutes.getJSONArray("success")

            response.appStateError = appState.getBoolean("error")
            response.appActive = appState.getBoolean("appActive")
            response.message = appState.getString("message")
            response.isDayTime = json.getBoolean("isDayTime")
            response.lastUpdate = appState.getLong("lastUpdate")

            for (i: Int in 0 until successAudiovisuals.length()){
                response.audiovisualsToDelete.add(successAudiovisuals.getString(i))
            }

            for (i: Int in 0 until successPoints.length()){
                response.pointsToDelete.add(successPoints.getString(i))
            }

            for (i: Int in 0 until successRutes.length()) {
                response.rutesToDelete.add(successRutes.getString(i))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ErrorInsertPStatics","Error inserting periodically statics : $e")
        }

        return response
    }

    fun getPOIS(): Pair<Boolean,ArrayList<POI>> {
        val arrayPois = ArrayList<POI>()

        try {
            val json = JSONObject(getJson(poiURL))
            val array = json.getJSONArray("puntos")
            for (i:Int in 0 until array.length()) {
                val puntoInteres = POI.jsonAReferencia(array.get(i) as JSONObject)
                if (puntoInteres != null) arrayPois.add(puntoInteres)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, arrayPois)
        }
        return Pair(true,arrayPois)
    }

    fun getAudiovisuals(): Pair<Boolean, ArrayList<Audiovisual>> {
        val arrayAudiovisuals = ArrayList<Audiovisual>()

        try {
            val json = JSONObject(getJson(audiovisualsURL))
            val array = json.getJSONArray("audiovisuales")
            for (i: Int in 0 until  array.length()){
                val audiovisual = Audiovisual.jsonAReferencia(array.get(i) as JSONObject)
                if (audiovisual != null) arrayAudiovisuals.add(audiovisual)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false,arrayAudiovisuals)
        }

        return Pair(true, arrayAudiovisuals)
    }

    fun getRoutes(): Pair<Boolean, ArrayList<Route>> {
        val arrayRutes = ArrayList<Route>()

        try {
            val json = JSONObject(getJson(rutesURL))
            val array = json.getJSONArray("rutas")

            for (i: Int in 0 until array.length()) {
                val ruta = Route.jsonAReferencia(array.get(i) as JSONObject)
                if (ruta != null) arrayRutes.add(ruta)
            }

        } catch (e: Exception){
            e.printStackTrace()
            return Pair(false,arrayRutes)
        }

        return Pair(true,arrayRutes)
    }

    private fun getJson(url: String): String {
        val contenidoRespuesta = StringBuilder()
        try {
            val urlConnection = URL(url).openConnection()
            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
            var line: String?
            do {
                line = bufferedReader.readLine()
                contenidoRespuesta.append(line + "\n")
            } while (line != null)
            bufferedReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return contenidoRespuesta.toString()
    }

    private fun getJsonPOST(url: String, postData: JSONObject): String {
        val contenidoRespuesta = StringBuilder()
        try {
            val boundary = "===" + System.currentTimeMillis() + "==="
            val urlConnection = URL(url).openConnection()

            urlConnection.useCaches = false
            urlConnection.doInput = true
            urlConnection.doOutput = true
            urlConnection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary)
            val outputStream = urlConnection.getOutputStream()
            val writer = PrintWriter(OutputStreamWriter(outputStream,"UTF-8"))

            addFormField("id",postData.getString("id"),writer, boundary)

            if (postData.has("points")) {
                addFormField("points",postData.getJSONArray("points").toString(),writer,boundary)
            }

            if (postData.has("audiovisuals")) {
                addFormField("audiovisuals",postData.getJSONArray("audiovisuals").toString(),writer, boundary)
            }

            if (postData.has("rutes")) {
                addFormField("rutes",postData.getJSONArray("rutes").toString(),writer, boundary)
            }

            writer.close()
            outputStream.close()

            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
            var line: String?
            do {
                line = bufferedReader.readLine()
                contenidoRespuesta.append(line + "\n")
            } while (line != null)
            bufferedReader.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return contenidoRespuesta.toString()
    }

    private fun addFormField(name: String, value: String, writer: PrintWriter, boundary: String) {
        val LINE_FEED = "\r\n"

        writer.append("--" + boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
            .append(LINE_FEED)
        writer.append("Content-Type: text/plain; charset=UTF-8").append(
            LINE_FEED)
        writer.append(LINE_FEED)
        writer.append(value).append(LINE_FEED)
        writer.flush()
    }

    companion object {

        private var instance: ServerServices? = null

        @Synchronized
        fun getInstance(context: Context): ServerServices {
            if (instance == null) {
                instance = ServerServices(context)
            }

            return instance as ServerServices
        }

    }

}



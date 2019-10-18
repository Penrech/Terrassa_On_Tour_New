package com.parcaudiovisualcatalunya.terrassaontour.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONException
import org.json.JSONObject

@Entity (tableName = "audiovisual_table")
class Audiovisual(@PrimaryKey var id: String,
                  var idPuntoAudiovisual: String,
                  var title: String,
                  var description: String,
                  var imgCabecera: String,
                  var imgCabeceraThumbnail: String,
                  var src: String,
                  var year: String,
                  var tipoMedio: String,
                  var formato: String,
                  var actores: ArrayList<String>,
                  var directores: ArrayList<String>,
                  var productoras: ArrayList<ClienteProductora>,
                  var clientes: ArrayList<ClienteProductora>,
                  var rutasAudiovisual: ArrayList<String>) {

    fun getEnum(type: String): TipoMedioAudiovisual {
        return TipoMedioAudiovisual.valueOf(type)
    }

    enum class TipoMedioAudiovisual(val type: String){
        VIDEO("1"),
        IMAGEN("2")
    }

    class ClienteProductora(var nombre: String, var link: String)

    companion object {
        fun jsonAReferencia(referencia: JSONObject) : Audiovisual? {
            try {
                val actor = ArrayList<String>()
                val director = ArrayList<String>()
                val productor = ArrayList<ClienteProductora>()
                val client = ArrayList<ClienteProductora>()
                val routes = ArrayList<String>()

                val actores = referencia.getJSONArray("actor")
                for (i: Int in 0 until actores.length()){
                    actor.add(actores[i] as String)
                }

                val directores = referencia.getJSONArray("director")
                for (i: Int in 0 until directores.length()){
                    director.add(directores[i] as String)
                }

                val productoras = referencia.getJSONArray("productora")
                for (i: Int in 0 until productoras.length()){
                    val productoraInfo = productoras.getJSONObject(i)
                    val productora = ClienteProductora(
                        productoraInfo.getString("nombre_productora"),
                        productoraInfo.getString("link_productora")
                    )
                    productor.add(productora)
                }

                val clientes = referencia.getJSONArray("cliente")
                for (i: Int in 0 until clientes.length()) {
                    val clienteInfo = clientes.getJSONObject(i)
                    val cliente = ClienteProductora(
                        clienteInfo.getString("nombre_cliente"),
                        clienteInfo.getString("link_cliente")
                    )
                    client.add(cliente)
                }

                val rutas = referencia.getJSONArray("rutas")
                for (i: Int in 0 until rutas.length()){
                    routes.add(rutas[i] as String)
                }

                return Audiovisual(referencia.getString("id_audiovisual"),
                    referencia.getString("id_punto_audiovisual"),
                    referencia.getString("title"),
                    referencia.getString("descripcion"),
                    referencia.getString("img_cabecera"),
                    referencia.getString("img_cabecera_thumbnail"),
                    referencia.getString("src"),
                    referencia.getString("year"),
                    referencia.getString("tipo_medio"),
                    referencia.getString("formato"),
                    actor,
                    director,
                    productor,
                    client,
                    routes)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}
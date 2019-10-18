package com.parcaudiovisualcatalunya.terrassaontour.data

import android.os.Build
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@Entity (tableName = "statics_table")
class Statics(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var model: String = Build.MODEL,
    var name: String = Build.DEVICE,
    var product: String = Build.PRODUCT,
    var savedOnRemoteServer: Boolean = false,
    var dayTime: Boolean = true,
    var appActive: Boolean = true,
    var appStateMessage: String? = null,
    var lastPositionRegistered: LatLng? = null,
    var lastServerUpdate: Long = 0,
    private var visitedPoints: ArrayList<VisitHistory> = arrayListOf(),
    private var visitedAudiovisuals: ArrayList<VisitHistory> = arrayListOf(),
    private var visitedRoutes: ArrayList<VisitHistory> = arrayListOf(),
    private var currentRoute: String? = null,
    private var visitedRouteAudiovisuals: ArrayList<AudiovisualFromRouteVisited> = arrayListOf()) {


    class VisitHistory(var id: String, var date: Long)
    class AudiovisualFromRouteVisited(var idAudiovisual: String, var visited: Boolean = false)
    class InsertStaticsResponse {

        var appStateError = true
        var appActive = true
        var message: String? = null
        var isDayTime = true
        var lastUpdate: Long? = null

        var audiovisualsToDelete = ArrayList<String>()
        var pointsToDelete = arrayListOf<String>()
        var rutesToDelete = arrayListOf<String>()
    }
}
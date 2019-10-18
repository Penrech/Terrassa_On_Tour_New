package com.parcaudiovisualcatalunya.terrassaontour.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.parcaudiovisualcatalunya.terrassaontour.data.Statics
import java.util.*
import kotlin.collections.ArrayList

@Dao
interface StaticsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun initStatics(statics: Statics)

    @Query("SELECT id FROM statics_table LIMIT 1")
    fun getStaticsID(): String?

    @Query("SELECT visitedPoints FROM statics_table LIMIT 1")
    fun getVisitedPoints(): ArrayList<Statics.VisitHistory>

    @Query("SELECT COUNT(*) FROM statics_table")
    fun areTherePreviousStaticInstances(): Int

    @Query("SELECT visitedAudiovisuals FROM statics_table LIMIT 1")
    fun getVisitedAudiovisuals(): ArrayList<Statics.VisitHistory>

    @Query("SELECT visitedRoutes FROM statics_table LIMIT 1")
    fun getVisitedRoutes(): ArrayList<Statics.VisitHistory>

    @Query("SELECT currentRoute FROM statics_table LIMIT 1")
    fun getCurrentRoute(): String?

    @Query("SELECT dayTime FROM statics_table LIMIT 1")
    fun isDayTime():Boolean?

    @Query("SELECT lastPositionRegistered FROM statics_table LIMIT 1")
    fun getLastPosition(): LatLng?

    @Query("UPDATE statics_table SET currentRoute = NULL")
    fun deleteCurrentRoute()

    @Query("UPDATE statics_table SET currentRoute = :newRute")
    fun setNewCurrentRoute(newRute: String)

    @Query("SELECT visitedRouteAudiovisuals FROM statics_table LIMIT 1")
    fun getCurrentRouteVisitedAudiovisuals(): ArrayList<Statics.AudiovisualFromRouteVisited>

    @Query("UPDATE statics_table SET visitedPoints = :visitedPointsUpdate")
    fun updateVisitedPoints(visitedPointsUpdate: ArrayList<Statics.VisitHistory>)

    @Query("UPDATE statics_table SET visitedAudiovisuals = :visitedAudiovisualsUpdate")
    fun updateVisitedAudiovisuals(visitedAudiovisualsUpdate: ArrayList<Statics.VisitHistory>)

    @Query("UPDATE statics_table SET visitedRoutes = :visitedRoutesUpdate")
    fun updateVisitedRoutes(visitedRoutesUpdate: ArrayList<Statics.VisitHistory>)

    @Query("UPDATE statics_table SET visitedRouteAudiovisuals = :currentRouteVisitedAudiovisuals")
    fun updateCurrentRouteVisiteAudiovisuals(currentRouteVisitedAudiovisuals: ArrayList<Statics.AudiovisualFromRouteVisited>)

    @Query("UPDATE statics_table SET dayTime = :newDayTime")
    fun updateDayTime(newDayTime: Boolean)

    @Query("UPDATE statics_table SET lastServerUpdate = :newServerUpdateTime")
    fun updateLastServerUpdate(newServerUpdateTime: Long)

    @Query("UPDATE statics_table SET lastPositionRegistered = :lastNewPosition")
    fun updateLastPosition(lastNewPosition: LatLng)

    @Query("UPDATE statics_table SET savedOnRemoteServer = :savedOnServerState")
    fun updateRemoveServerSync(savedOnServerState: Boolean)

    @Query("SELECT dayTime FROM statics_table LIMIT 1")
    fun getDayTimeChanges(): LiveData<Boolean>

    @Query("SELECT lastServerUpdate FROM statics_table LIMIT 1")
    fun getServerDataUpdateChanges(): LiveData<Long>

    @Query("SELECT appActive FROM statics_table LIMIT 1")
    fun getServerAppStateUpdateChanges(): LiveData<Boolean>

    @Query("SELECT appStateMessage FROM statics_table LIMIT 1")
    fun getAppOffMessage(): String?

    @Query("SELECT savedOnRemoteServer FROM statics_table LIMIT 1")
    fun getIfStaticsAreSavedOnServer(): Boolean

    @Query("SELECT * FROM statics_table LIMIT 1")
    fun getTemporaryCopyOfStaticsToSend(): Statics?

    @Query("SELECT lastServerUpdate FROM statics_table LIMIT 1")
    fun getLastUpdatedTime(): Long?

    @Transaction
    fun cleanPoints(pointsList: List<String>){
        val currentPoints = getVisitedPoints()
        pointsList.forEach { id->
            val filter = currentPoints.firstOrNull { it.id == id }
            currentPoints.remove(filter)
        }
        updateVisitedPoints(currentPoints)
    }

    @Transaction
    fun cleanAudiovisuals(audiovisualsList: List<String>){
        val currentAudiovisuals = getVisitedAudiovisuals()
        audiovisualsList.forEach { id->
            val filter = currentAudiovisuals.firstOrNull { it.id == id }
            if (filter != null) currentAudiovisuals.remove(filter)
        }
        updateVisitedAudiovisuals(currentAudiovisuals)
    }

    @Transaction
    fun cleanRoutes(routesList: List<String>){
        val currentRoutes = getVisitedRoutes()
        routesList.forEach { id->
            val filter = currentRoutes.firstOrNull { it.id == id }
            if (filter != null) currentRoutes.remove(filter)
        }
        updateVisitedRoutes(currentRoutes)
    }

    @Transaction
    fun addPointVisit(pointID: String){
        val rightNow = Calendar.getInstance()
        val currentPoints = getVisitedPoints()
        currentPoints.add(Statics.VisitHistory(pointID,rightNow.timeInMillis))
        updateVisitedPoints(currentPoints)
    }

    @Transaction
    fun addAudiovisualVisit(audiovisualID: String){
        val rightNow = Calendar.getInstance()
        val currentAudiovisuals = getVisitedAudiovisuals()
        currentAudiovisuals.add(Statics.VisitHistory(audiovisualID,rightNow.timeInMillis))
        updateVisitedAudiovisuals(currentAudiovisuals)

        getCurrentRoute()?.let {
            checkIfAudiovisualIsInCurrentRoute(audiovisualID)
            checkIfRouteIsCompleted()
        }
    }

    @Transaction
    fun addRouteVisit(routeID: String){
        val rightNow = Calendar.getInstance()
        val currentRoutes = getVisitedRoutes()
        currentRoutes.add(Statics.VisitHistory(routeID,rightNow.timeInMillis))
        updateVisitedRoutes(currentRoutes)
    }

    @Transaction
    fun isSameRoute(ruteID: String, routeAudiovisuals: List<String>):Boolean{
        val currentRoute = getCurrentRoute()
        if (currentRoute == null || currentRoute != ruteID) return false

        val flatVisitedIds = getCurrentRouteVisitedAudiovisuals().map { it.idAudiovisual }
        val sum = routeAudiovisuals + flatVisitedIds
        val difference = sum.groupBy { it }
            .filter { it.value.size == 1 }
            .flatMap { it.value }

        return difference.isEmpty()
    }

    @Transaction
    fun checkIfAudiovisualIsInCurrentRoute(audiovisualID: String){
        val currentRouteVisitedAudiovisuals = getCurrentRouteVisitedAudiovisuals()
        val index = currentRouteVisitedAudiovisuals.asSequence().map { it.idAudiovisual }.indexOf(audiovisualID)
        if (index != -1){
            currentRouteVisitedAudiovisuals[index].visited = true
            updateCurrentRouteVisiteAudiovisuals(currentRouteVisitedAudiovisuals)
        }
    }

    @Transaction
    fun checkIfRouteIsCompleted(){
        val currentRouteVisitedAudiovisuals = getCurrentRouteVisitedAudiovisuals()
        currentRouteVisitedAudiovisuals.forEach {
            if (!it.visited) {
                return
            }
        }

        getCurrentRoute()?.let {
            addRouteVisit(it)

            currentRouteVisitedAudiovisuals.forEach { audiovisualFromRoute ->
                audiovisualFromRoute.visited = false
            }

            updateCurrentRouteVisiteAudiovisuals(currentRouteVisitedAudiovisuals)
        }
    }

    @Transaction
    fun removeCurrentRoute(){
        if (getCurrentRoute() == null) return

        deleteCurrentRoute()
        val currentRouteVisiteAudiovisuals = getCurrentRouteVisitedAudiovisuals()
        currentRouteVisiteAudiovisuals.clear()
        updateCurrentRouteVisiteAudiovisuals(currentRouteVisiteAudiovisuals)
    }

    @Transaction
    fun setCurrentRoute(ruteID: String, ruteAudivisuals: List<String>){
        val currentVisitedRouteAudiovisuals = getCurrentRouteVisitedAudiovisuals()

        if (!isSameRoute(ruteID,ruteAudivisuals)) currentVisitedRouteAudiovisuals.clear()

        val flatVisitedIds = currentVisitedRouteAudiovisuals.map { it.idAudiovisual }
        val sum = ruteAudivisuals + flatVisitedIds

        val difference = sum.groupBy { it }
            .filter { it.value.size == 1 }
            .flatMap { it.value }

        val sum2 = ruteAudivisuals + difference

        val addIDs = sum2.groupBy { it }
            .filterNot { it.value.size == 1 }
            .flatMap { it.value }.distinct()

        val removeIDs = difference - addIDs

        addIDs.forEach {
            currentVisitedRouteAudiovisuals.add(Statics.AudiovisualFromRouteVisited(it))
        }

        removeIDs.forEach { idToRemove ->
            currentVisitedRouteAudiovisuals.removeAll { it.idAudiovisual == idToRemove }
        }

        setNewCurrentRoute(ruteID)
        updateCurrentRouteVisiteAudiovisuals(currentVisitedRouteAudiovisuals)
    }

    @Transaction
    fun updateStaticsAfterServerUpdate(data: Statics.InsertStaticsResponse){
        isDayTime()?.let {
            if (it != data.isDayTime) updateDayTime(data.isDayTime)
        }

        if (data.lastUpdate != null && getLastUpdatedTime() != null && data.lastUpdate != getLastUpdatedTime()){
            updateLastServerUpdate(data.lastUpdate!!)
        }
        cleanAudiovisuals(data.audiovisualsToDelete)
        cleanPoints(data.pointsToDelete)
        cleanRoutes(data.rutesToDelete)
    }

    @Transaction
    fun InitStaticsOnAppStart(){
        if (areTherePreviousStaticInstances() == 0) {
            initStatics(Statics())
        }
    }
}
package com.parcaudiovisualcatalunya.terrassaontour.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.parcaudiovisualcatalunya.terrassaontour.data.local.POIDao
import com.parcaudiovisualcatalunya.terrassaontour.data.local.TOTDatabase
import com.parcaudiovisualcatalunya.terrassaontour.data.remote.ServerServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class POIRepository(application: Application) {

    private var poiDao: POIDao
    private var serverService: ServerServices
    private var allPois: LiveData<List<POI>>

    init {
        val database = TOTDatabase.getInstance(application)
        poiDao = database.poiDao()
        allPois = poiDao.getAllPois()
        serverService = ServerServices.getInstance(application)
    }

    private fun insertNewPois(pois: Array<POI>){
        CoroutineScope(IO).launch {
            poiDao.insertAndDeletePoisTransaction(pois)
        }
    }

    fun deleteSpecificPoi(poisToDelete: ArrayList<String>){
        CoroutineScope(IO).launch {
            poiDao.deleteSpecificPOI(poisToDelete)
        }
    }

    suspend fun selectPointById(poiID: String): POI?{
        val poi = CoroutineScope(IO).async {
            poiDao.selectPoiById(poiID)
        }

        return poi.await()
    }

    suspend fun selectPointByTarget(targetID: String) : POI? {
        val poi = CoroutineScope(IO).async {
            poiDao.selectPoiByTarget(targetID)
        }

        return poi.await()
    }

    fun getAllPois(): LiveData<List<POI>>{
        return allPois
    }

    suspend fun getPoisFromServerDB(): Boolean{
        val result = CoroutineScope(IO).async {
            serverService.getPOIS()
        }

        if (!result.await().first) return false

        insertNewPois(result.await().second.toTypedArray())

        return true
    }

}
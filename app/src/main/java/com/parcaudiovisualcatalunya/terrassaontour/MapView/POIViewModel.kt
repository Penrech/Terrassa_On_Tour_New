package com.parcaudiovisualcatalunya.terrassaontour.MapView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.parcaudiovisualcatalunya.terrassaontour.data.POI
import com.parcaudiovisualcatalunya.terrassaontour.data.POIRepository

class POIViewModel(application: Application): AndroidViewModel(application) {

    private val repository: POIRepository = POIRepository(application)
    private val allPois: LiveData<List<POI>>

    init {
        allPois = repository.getAllPois()
    }

    fun deleteSpecificPoi(poisToDelete: ArrayList<String>){
        repository.deleteSpecificPoi(poisToDelete)
    }

    fun getAllPois(): LiveData<List<POI>>{
        return allPois
    }

    suspend fun selectPointByID(poiID: String): POI? {
        return repository.selectPointById(poiID)
    }

    suspend fun selectPointByTarget(targetID: String): POI?{
        return repository.selectPointByTarget(targetID)
    }

    suspend fun getPoisFromServerDB(): Boolean {
        return repository.getPoisFromServerDB()
    }
}
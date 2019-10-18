package com.parcaudiovisualcatalunya.terrassaontour.MapView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.parcaudiovisualcatalunya.terrassaontour.data.Route
import com.parcaudiovisualcatalunya.terrassaontour.data.RouteRepository

class RouteViewModel(application: Application): AndroidViewModel(application) {

    private val repository: RouteRepository =  RouteRepository(application)
    private val allRoutes: LiveData<List<Route>>

    init {
        allRoutes = repository.getAllRoutes()
    }

    fun deleteSpecificRoutes(routesToDelete: ArrayList<String>){
        repository.deleteSpecificRoutes(routesToDelete)
    }

    fun getAllRoutes(): LiveData<List<Route>>{
        return allRoutes
    }

    suspend fun selectRouteByID(routeID: String): Route? {
        return repository.selectRouteById(routeID)
    }

    suspend fun getRoutesFromsServerDB() : Boolean {
        return repository.getRoutesFromServerDB()
    }
}
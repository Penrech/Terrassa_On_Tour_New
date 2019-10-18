package com.parcaudiovisualcatalunya.terrassaontour.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.parcaudiovisualcatalunya.terrassaontour.data.Route

@Dao
interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoutes(vararg routes: Route)

    @Query("DELETE FROM route_table WHERE id in (:routesToDelete) ")
    fun deleteSpecificRoutes(routesToDelete: ArrayList<String>)

    @Query("SELECT * FROM route_table WHERE id = :routeId")
    fun selectRouteById(routeId: String): Route?

    @Query("DELETE FROM route_table")
    fun deleteAllRoutes()

    @Query("SELECT * FROM route_table ORDER BY id")
    fun getAllRoutes(): LiveData<List<Route>>

    @Transaction
    fun insertAndDeleteRoutesTransaction(routes: Array<Route>) {
        deleteAllRoutes()
        insertRoutes(*routes)
    }
}
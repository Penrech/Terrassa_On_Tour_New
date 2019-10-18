package com.parcaudiovisualcatalunya.terrassaontour.data.local

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.parcaudiovisualcatalunya.terrassaontour.data.POI

interface POIDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPois(vararg pois: POI)

    @Query("DELETE FROM point_table WHERE id in (:poisToDelete) ")
    fun deleteSpecificPOI(poisToDelete: ArrayList<String>)

    @Query("SELECT * FROM point_table WHERE id = :poiID")
    fun selectPoiById(poiID: String): POI?

    @Query("SELECT * FROM point_table WHERE idTargetDia = :targetID OR idTargetNoche = :targetID")
    fun selectPoiByTarget(targetID: String): POI?

    @Query("DELETE FROM point_table")
    fun deleteAllPois()

    @Query("SELECT * FROM point_table ORDER BY id")
    fun getAllPois(): LiveData<List<POI>>

    @Transaction
    fun insertAndDeletePoisTransaction(pois: Array<POI>) {
        deleteAllPois()
        insertPois(*pois)
    }
}
package com.parcaudiovisualcatalunya.terrassaontour.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.parcaudiovisualcatalunya.terrassaontour.data.Audiovisual
import com.parcaudiovisualcatalunya.terrassaontour.data.POI
import com.parcaudiovisualcatalunya.terrassaontour.data.Route
import com.parcaudiovisualcatalunya.terrassaontour.data.Statics
import parcaudiovisual.terrassaontour.database.StaticsDao

@Database (entities = [Audiovisual::class, POI::class, Route::class, Statics::class], version = 1)
abstract class TOTDatabase: RoomDatabase(){

    abstract fun poiDao(): POIDao
    abstract fun routeDao(): RouteDao
    abstract fun staticsDao(): StaticsDao
    abstract fun audiovisualDao(): AudiovisualDao

    companion object {

        private var instance: TOTDatabase? = null

        @Synchronized
        fun getInstance(context: Context): TOTDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    TOTDatabase::class.java, "tot_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }
            return instance as TOTDatabase
        }

        private val roomCallback = object : RoomDatabase.Callback(){
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }

    }

}
package com.parcaudiovisualcatalunya.terrassaontour.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.parcaudiovisualcatalunya.terrassaontour.data.local.StaticsDao
import com.parcaudiovisualcatalunya.terrassaontour.data.local.TOTDatabase
import com.parcaudiovisualcatalunya.terrassaontour.data.remote.ServerServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class StaticsRepository(application: Application) {

    private var staticsDao: StaticsDao
    private var serverServices: ServerServices
    private var dayTimeChange: LiveData<Boolean>
    private var dataIsUpdated: LiveData<Long>
    private var appStateChange: LiveData<Boolean>

    init {
        val database = TOTDatabase.getInstance(application)
        staticsDao = database.staticsDao()
        dayTimeChange = staticsDao.getDayTimeChanges()
        dataIsUpdated = staticsDao.getServerDataUpdateChanges()
        appStateChange = staticsDao.getServerAppStateUpdateChanges()
        serverServices = ServerServices.getInstance(application)
    }

    fun initStaticsOnAppStart() {
        CoroutineScope(IO).launch {
            staticsDao.InitStaticsOnAppStart()
        }
    }

    fun updateStaticsWithServer(){
        CoroutineScope(IO).launch {
            val id = staticsDao.getStaticsID() ?: return@launch

            if (!staticsDao.getIfStaticsAreSavedOnServer()) {

                val tempStatics = staticsDao.getTemporaryCopyOfStaticsToSend() ?: return@launch

                val result = serverServices.insertUserIfNeeded(
                    tempStatics.id,
                    tempStatics.model,
                    tempStatics.name,
                    tempStatics.product
                )

                if (!result.first) return@launch

                staticsDao.updateRemoveServerSync(true)
            }

            createDataFormToSendToServer(id)
        }

    }

    fun getDayTimeUpdateChanges(): LiveData<Boolean>{
        return dayTimeChange
    }

    fun getDataUpdateChanges(): LiveData<Long>{
        return dataIsUpdated
    }

    fun getAppStateUpdateChanges(): LiveData<Boolean>{
        return appStateChange
    }

    suspend fun getCurrentRoute(): String?{
        return withContext(CoroutineScope(IO).coroutineContext) {
            staticsDao.getCurrentRoute()
        }
    }

    fun addPointVisit(pointID: String){
        CoroutineScope(IO).launch {
            staticsDao.addPointVisit(pointID)
        }
    }

    fun addRouteVisit(routeID: String){
        CoroutineScope(IO).launch {
            staticsDao.addRouteVisit(routeID)
        }
    }

    fun addAudiovisualVisit(audiviosualID: String){
        CoroutineScope(IO).launch {
            staticsDao.addAudiovisualVisit(audiviosualID)
        }
    }

    fun setCurrentRoute(ruteID: String, ruteAudiovisuals: List<String>){
        CoroutineScope(IO).launch {
            staticsDao.setCurrentRoute(ruteID,ruteAudiovisuals)
        }
    }

    fun removeCurrentRoute(){
        CoroutineScope(IO).launch {
            staticsDao.removeCurrentRoute()
        }
    }

    suspend fun isSameRoute(ruteID: String, ruteAudiovisuals: List<String>): Boolean{
        return withContext(CoroutineScope(IO).coroutineContext) {
            staticsDao.isSameRoute(ruteID,ruteAudiovisuals)
        }
    }


    private suspend fun createDataFormToSendToServer(staticsID: String){
        val postData = JSONObject()
        postData.put("id",staticsID)
        val pointsArray = JSONArray()
        val rutesArray = JSONArray()
        val audiovisualsArray = JSONArray()

        staticsDao.getVisitedPoints().forEach { pointsVisited->
            val pointsVisitedJsonObject = JSONObject()
            pointsVisitedJsonObject.put("id",pointsVisited.id)
            pointsVisitedJsonObject.put("date",pointsVisited.date.toString())
            pointsArray.put(pointsVisitedJsonObject)
        }

        staticsDao.getVisitedAudiovisuals().forEach { audiovisualVisited ->
            val audiovisualVisitedJsonObject = JSONObject()
            audiovisualVisitedJsonObject.put("id",audiovisualVisited.id)
            audiovisualVisitedJsonObject.put("date",audiovisualVisited.date.toString())
            audiovisualsArray.put(audiovisualVisitedJsonObject)
        }

        staticsDao.getVisitedRoutes().forEach { ruteVisited->
            val ruteVisitedJsonObject = JSONObject()
            ruteVisitedJsonObject.put("id",ruteVisited.id)
            ruteVisitedJsonObject.put("date",ruteVisited.date.toString())
            rutesArray.put(ruteVisitedJsonObject)
        }

        if (pointsArray.length() > 0) postData.put("points",pointsArray)
        if (audiovisualsArray.length() > 0) postData.put("audiovisuals",audiovisualsArray)
        if (rutesArray.length() > 0) postData.put("rutes", rutesArray)

        val result = serverServices.insertPeriodicallyStatics(postData)

        if (!result.appStateError){
            staticsDao.updateStaticsAfterServerUpdate(result)

        }
    }


}
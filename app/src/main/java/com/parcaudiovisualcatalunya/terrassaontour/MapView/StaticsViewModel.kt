package com.parcaudiovisualcatalunya.terrassaontour.MapView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.parcaudiovisualcatalunya.terrassaontour.data.StaticsRepository

class StaticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StaticsRepository = StaticsRepository(application)


}
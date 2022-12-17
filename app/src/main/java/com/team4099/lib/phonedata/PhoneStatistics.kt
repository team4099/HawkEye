package com.team4099.lib.phonedata

import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.team4099.lib.photonvision.networking.TimedTaskManager

@RequiresApi(Build.VERSION_CODES.S)
object PhoneStatistics {
    lateinit var powerManager: PowerManager

    init {
        TimedTaskManager.Singleton.instance.addTask("PhoneStatistics", this::getDischargePrediction, 5000);
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getDischargePrediction(){
        Log.i("PhoneStatistics", "Discharge prediction: " + (powerManager.batteryDischargePrediction?.toHours()
            ?: "-1337"));
    }
}
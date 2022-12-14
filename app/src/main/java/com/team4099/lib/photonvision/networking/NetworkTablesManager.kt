package com.team4099.lib.photonvision.networking

import android.util.Log
import edu.wpi.first.networktables.NetworkTableInstance

object NetworkTablesManager {
    private val ntInstance = NetworkTableInstance.getDefault()
    private val rootTableName = "/photonvision"
    val hawkeyeTable = ntInstance.getTable(rootTableName)

    private var isRetryingConnection = false

    init{
        TimedTaskManager.Singleton.instance.addTask("NTManager", this::ntTick, 5000);
        ntInstance.startClient4(HawkeyeConfig.cameraName);
        if (HawkeyeConfig.staticIp != null){
            ntInstance.setServer(HawkeyeConfig.staticIp)
        } else {
            ntInstance.setServerTeam(HawkeyeConfig.teamNumber)
        }
    }

    private fun ntTick() {
        if (ntInstance.isConnected) {
            isRetryingConnection = false
        }
        if (!ntInstance.isConnected && !isRetryingConnection) {
            isRetryingConnection = true
            Log.e("NetworkTablesManager", "Could not connect to the robot! Will retry in the background...")
//            logger.error(
//                    "[NetworkTablesManager] Could not connect to the robot! Will retry in the background...")
        }
        Log.i("NetworkTablesManager", "Connected to NetworkTables: " + ntInstance.isConnected)
    }



}
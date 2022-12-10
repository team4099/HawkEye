package com.team4099.lib.networking

import android.util.Log
import edu.wpi.first.networktables.NetworkTableInstance

object NetworkTablesManager {
    private val ntInstance = NetworkTableInstance.getDefault()
    private val rootTableName = "/photonvision/"
    val hawkeyeTable = ntInstance.getTable(rootTableName)

    private var isRetryingConnection = false

    init{
        TimedTaskManager.Singleton.instance.addTask("NTManager", this::ntTick, 5000);
    }


    fun setConfig(config: NetworkConfig){
        if (NetworkConfig.runNTServer){
            setServerMode()
        } else {
            setClientMode(NetworkConfig.teamNumber)
        }

    }

    private fun setClientMode(teamNumber: Int){
        ntInstance.stopServer()

        ntInstance.startClient3("")
    }

    private fun setServerMode(){
        ntInstance.stopClient()
        ntInstance.startServer()
    }

    private fun ntTick() {
        if (!ntInstance.isConnected
                && NetworkConfig.runNTServer
        ) {
            setConfig(NetworkConfig)
        }
        if (!ntInstance.isConnected && !isRetryingConnection) {
            isRetryingConnection = true
            Log.e("NetworkTablesManager", "Could not connect to the robot! Will retry in the background...")
//            logger.error(
//                    "[NetworkTablesManager] Could not connect to the robot! Will retry in the background...")
        }
    }



}
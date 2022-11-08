package com.team4099.lib.networking

import edu.wpi.first.networktables.NetworkTableInstance

class NetworkTablesManager {
    private val ntInstance = NetworkTableInstance.getDefault()
    private val hawkeyeRootTableName = "/hawkeye"
    val hawkeyeTable = ntInstance.getTable(hawkeyeRootTableName)

    private var isRetryingConnection = false

    init{
        TimedTaskManager.Singleton.instance.addTask("NTManager", this::ntTick, 5000);
    }

    companion object {
        val INSTANCE: NetworkTablesManager? = null
            get(){
                if (field == null){
                    return NetworkTablesManager()
                } else {
                    return field
                }
            }
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

        ntInstance.startClientTeam(teamNumber)
        ntInstance.startDSClient()
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
//            logger.error(
//                    "[NetworkTablesManager] Could not connect to the robot! Will retry in the background...")
        }
    }



}
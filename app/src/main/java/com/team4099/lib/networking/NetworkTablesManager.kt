package com.team4099.lib.networking

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.networktables.PubSubOption

object NetworkTablesManager {
    private val ntInstance = NetworkTableInstance.getDefault()
    private val rootTableName = "/photonvision/"
    val hawkeyeTable = ntInstance.getTable(rootTableName)
    var xSub = hawkeyeTable.getDoubleTopic("x").publish(PubSubOption.periodic(1.0));
    var ySub = hawkeyeTable.getDoubleTopic("y").publish(PubSubOption.periodic(1.0));


    private var isRetryingConnection = false

    init{
        TimedTaskManager.Singleton.instance.addTask("NTManager", this::ntTick, 5000);
        ntInstance.startClient3("hawkeye test");
        ntInstance.setServer("127.0.0.1")
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

        ntInstance.startClient3("127.0.0.1")
        ntInstance.startServer("127.0.0.1")
    }

    private fun setServerMode(){
        ntInstance.stopClient()
        ntInstance.startServer()
    }

    private fun ntTick() {
//        if (!ntInstance.isConnected) {
//            setConfig(NetworkConfig)
//        }
//        if (!ntInstance.isConnected && !isRetryingConnection) {
//            isRetryingConnection = true
//            Log.e("NetworkTablesManager", "Could not connect to the robot! Will retry in the background...")
////            logger.error(
////                    "[NetworkTablesManager] Could not connect to the robot! Will retry in the background...")
//        }
        ySub.set(101.0)
        xSub.set(101231.0)
        println("X: " + xSub.topic + " Y: " + xSub.topic)
    }



}
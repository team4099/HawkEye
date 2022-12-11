package com.team4099.lib.networking


object NetworkConfig{
        var teamNumber: Int = -1337
        //    var connectionType: NetworkMode = NetworkMode.DHCP
        var staticIp: String? = "10.0.2.2"
        var cameraName: String = "hawkeye"
        var runNTServer: Boolean = false
        var shouldManage: Boolean = false

//    fun toHashMap(): HashMap<String, Any?> {
//        val tmp = HashMap<String, Any?>()
//        tmp["teamNumber"] = teamNumber
//        tmp["supported"] = shouldManage
////        tmp["connectionType"] = connectionType.ordinal()
//        tmp["staticIp"] = staticIp
//        tmp["hostname"] = hostname
//        tmp["runNTServer"] = runNTServer
//        return tmp
//    }
//
//    object {
//        fun fromHashMap(map: Map<String?, Any>): NetworkConfig {
//            // teamNumber (int), supported (bool), connectionType (int),
//            // staticIp (str), netmask (str), hostname (str)
//            val ret = NetworkConfig(
//                    map["teamNumber"].toString().toInt(),
//                    map["staticIp"] as String?,
//                    map["hostname"] as String?,
//                    (map["runNTServer"] as Boolean?)!!,
//                    ((map["supported"] as Boolean?)!!)
//            )
//            return ret
//        }
//    }
}
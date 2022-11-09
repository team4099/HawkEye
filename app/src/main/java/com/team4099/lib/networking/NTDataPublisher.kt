package com.team4099.lib.networking

import com.team4099.lib.HawkeyeResult
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableEntry

class NTDataPublisher(cameraNickname: String) {
    private val rootTable: NetworkTable = NetworkTablesManager.INSTANCE!!.hawkeyeTable
    private var subTable: NetworkTable? = null

    private var rawBytesEntry: NetworkTableEntry? = null //do I really want to implement this tho

    private var latencyMillisEntry: NetworkTableEntry? = null
    private var hasTargetEntry: NetworkTableEntry? = null
    private var targetPitchEntry: NetworkTableEntry? = null
    private var targetYawEntry: NetworkTableEntry? = null
    private var targetAreaEntry: NetworkTableEntry? = null
    private var targetPoseEntry: NetworkTableEntry? = null
    private var targetSkewEntry: NetworkTableEntry? = null

    // The raw position of the best target, in pixels.
    private var bestTargetPosX: NetworkTableEntry? = null
    private var bestTargetPosY: NetworkTableEntry? = null

    init {
        updateCameraNickname(cameraNickname)
        updateEntries()
    }

    fun updateCameraNickname(cameraNickname: String){
        removeEntries()
        subTable = rootTable.getSubTable(cameraNickname)
        updateEntries()
    }

    private fun removeEntries() {
        if (rawBytesEntry != null) rawBytesEntry!!.delete()
        if (latencyMillisEntry != null) latencyMillisEntry!!.delete()
        if (hasTargetEntry != null) hasTargetEntry!!.delete()
        if (targetPitchEntry != null) targetPitchEntry!!.delete()
        if (targetAreaEntry != null) targetAreaEntry!!.delete()
        if (targetYawEntry != null) targetYawEntry!!.delete()
        if (targetPoseEntry != null) targetPoseEntry!!.delete()
        if (targetSkewEntry != null) targetSkewEntry!!.delete()
        if (bestTargetPosX != null) bestTargetPosX!!.delete()
        if (bestTargetPosY != null) bestTargetPosY!!.delete()
    }

    private fun updateEntries(){
        val updateCopy: NetworkTable = subTable!!

        rawBytesEntry = updateCopy.getEntry("rawBytes")

        latencyMillisEntry = updateCopy.getEntry("latencyMillis");
        hasTargetEntry = updateCopy.getEntry("hasTarget");

        targetPitchEntry = updateCopy.getEntry("targetPitch");
        targetAreaEntry = updateCopy.getEntry("targetArea");
        targetYawEntry = updateCopy.getEntry("targetYaw");
        targetPoseEntry = updateCopy.getEntry("targetPose");
        targetSkewEntry = updateCopy.getEntry("targetSkew");

        bestTargetPosX = updateCopy.getEntry("targetPixelsX");
        bestTargetPosY = updateCopy.getEntry("targetPixelsY");
    }

    fun accept(result: HawkeyeResult){
        // TODO add rawbytes stuff https://github.com/PhotonVision/photonvision/blob/7b6afd545bf824328e9b7e054a2cf9d9f4a026f6/photon-core/src/main/java/org/photonvision/common/dataflow/networktables/NTDataPublisher.java#L171

        latencyMillisEntry?.forceSetDouble(result.latencyMS)
        hasTargetEntry?.forceSetBoolean(result.hasTargets)

        if (result.hasTargets){
            var bestTarget = result.targets[0]

//            targetPitchEntry?.forceSetDouble(bestTarget)
        }


    }




}
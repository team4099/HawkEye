package com.team4099.lib.photonvision.networking

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableEntry

class NTDataPublisher(cameraNickname: String) {
    private val rootTable: NetworkTable = NetworkTablesManager.hawkeyeTable
    private var subTable: NetworkTable? = null

    private var rawBytesEntry: NetworkTableEntry? = null

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
        rawBytesEntry?.unpublish()
        latencyMillisEntry?.unpublish()
        hasTargetEntry?.unpublish()
        targetPitchEntry?.unpublish()
        targetAreaEntry?.unpublish()
        targetYawEntry?.unpublish()
        targetPoseEntry?.unpublish()
        targetSkewEntry?.unpublish()
        bestTargetPosX?.unpublish()
        bestTargetPosY?.unpublish()
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
        val packet = Packet(result.packetSize)
        result.populatePacket(packet)
        rawBytesEntry?.setRaw(packet.data)

        latencyMillisEntry?.setDouble(result.latencyMS)
        hasTargetEntry?.setBoolean(result.hasTargets)

        if (result.hasTargets) {
            val bestTarget = result.trackedTargets[0]
            targetPitchEntry?.setDouble(bestTarget.pitch)
            targetYawEntry?.setDouble(bestTarget.yaw)
            targetAreaEntry?.setDouble(bestTarget.area)
            targetSkewEntry?.setDouble(bestTarget.skew)
            val pose = bestTarget.bestCameraToTarget
            targetPoseEntry?.setDoubleArray(
                doubleArrayOf(
                    pose.translation.first, // x
                    pose.translation.second, // y
                    pose.translation.third, // z
                    pose.quat.w,
                    pose.quat.x,
                    pose.quat.y,
                    pose.quat.z
                )
            )
            val targetOffsetPoint = bestTarget.center
            bestTargetPosX?.setDouble(targetOffsetPoint.first)
            bestTargetPosY?.setDouble(targetOffsetPoint.second)
        } else {
            targetPitchEntry?.setDouble(0.0)
            targetYawEntry?.setDouble(0.0)
            targetAreaEntry?.setDouble(0.0)
            targetSkewEntry?.setDouble(0.0)
            targetPoseEntry?.setDoubleArray(doubleArrayOf(0.0, 0.0, 0.0))
            bestTargetPosX?.setDouble(0.0)
            bestTargetPosY?.setDouble(0.0)
        }
        rootTable.instance.flush()
    }




}
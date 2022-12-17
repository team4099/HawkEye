package com.team4099.lib.photonvision.networking

import com.team4099.lib.geo.MathUtils
import com.team4099.lib.photonvision.PhotonTrackedTarget
import edu.umich.eecs.april.apriltag.ApriltagPose
import edu.wpi.first.math.geometry.Transform3d

class HawkeyeResult(val latencyMS: Double, targets: List<ApriltagPose>){
    val hasTargets: Boolean = targets.isNotEmpty()
    var trackedTargets: List<PhotonTrackedTarget>
    init{
        trackedTargets = targets.map { PhotonTrackedTarget(it) }
        for (target in trackedTargets){
            val eulerAngles = MathUtils.quaternionToEulerAngles(target.bestCameraToTarget.rotation.quaternion)
            target.yaw = eulerAngles.yaw
            target.pitch = eulerAngles.pitch

            val correctedBestPose = MathUtils.convertEDNPoseToNWUPose(
                MathUtils.convertAprilTagOutputToEDN(target.bestCameraToTarget)
            )
            val correctedAltPose = MathUtils.convertEDNPoseToNWUPose(
                MathUtils.convertAprilTagOutputToEDN(target.alternateCameraToTarget)
            )
            target.bestCameraToTarget = Transform3d(correctedBestPose.translation, correctedBestPose.rotation)
            target.alternateCameraToTarget = Transform3d(correctedAltPose.translation, correctedAltPose.rotation)
        }
    }

    fun populatePacket(packet: Packet): Packet {
        packet.encode(latencyMS)
        packet.encode(trackedTargets.size.toByte())
        for (target in trackedTargets){
            target.populatePacket(packet)
        }
        return packet
    }

    val packetSize: Int = targets.size * PhotonTrackedTarget.PACK_SIZE_BYTES + 8 + 2
}

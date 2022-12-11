package com.team4099.lib.photonvision.networking

import com.team4099.lib.photonvision.PhotonTrackedTarget
import edu.umich.eecs.april.apriltag.ApriltagPose

class HawkeyeResult(val latencyMS: Double, targets: List<ApriltagPose>){
    val hasTargets: Boolean = targets.isNotEmpty()
    val trackedTargets: List<PhotonTrackedTarget>
    init{
        trackedTargets = targets.map { PhotonTrackedTarget(it) }
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

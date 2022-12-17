package com.team4099.lib.photonvision

import com.team4099.lib.geo.MathUtils
import com.team4099.lib.photonvision.networking.Packet
import edu.umich.eecs.april.apriltag.ApriltagPose
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.geometry.Translation3d
import java.util.*

class PhotonTrackedTarget {
    var yaw: Double

    var pitch: Double

    val area: Double

    val skew: Double

    /** Get the Fiducial ID, or -1 if not set.  */
    val fiducialId: Int

    /**
     * Get the transform that maps camera space (X = forward, Y = left, Z = up) to object/fiducial tag
     * space (X forward, Y left, Z up) with the lowest reprojection error
     */
    var bestCameraToTarget: Transform3d

    /**
     * Get the transform that maps camera space (X = forward, Y = left, Z = up) to object/fiducial tag
     * space (X forward, Y left, Z up) with the highest reprojection error
     */
    var alternateCameraToTarget: Transform3d

    /**
     * Get the ratio of pose reprojection errors, called ambiguity. Numbers above 0.2 are likely to be
     * ambiguous. -1 if invalid.
     */
    val poseAmbiguity: Double

    /**
     * Return a list of the 4 corners in image space (origin top left, x left, y down), in no
     * particular order, of the minimum area bounding rectangle of this target
     */
    val corners: List<TargetCorner>

    /**
     * (x,y) The center of the detection in image pixel coordinates.
     */
    val center: Pair<Double, Double>

    /** Construct a tracked target, given exactly 4 corners  */
    constructor(
        yaw: Double,
        pitch: Double,
        area: Double,
        skew: Double,
        id: Int,
        pose: Transform3d,
        altPose: Transform3d,
        ambiguity: Double,
        corners: List<TargetCorner>,
        center: Pair<Double,Double>
    ) {
        assert(corners.size == 4)
        this.yaw = yaw
        this.pitch = pitch
        this.area = area
        this.skew = skew
        this.fiducialId = id
        this.bestCameraToTarget = pose
        this.alternateCameraToTarget = altPose
        this.corners = corners
        this.poseAmbiguity = ambiguity
        this.center = center
    }

    constructor(pose: ApriltagPose) {
        assert(pose.corners.size == 8)
        val bestPoseQuat = MathUtils.rotationMatrixToQuaternion(pose.rotation_1)
        val altPoseQuat = MathUtils.rotationMatrixToQuaternion(pose.rotation_2)
        this.yaw = 0.0
        this.pitch = 0.0
        this.area = 0.0
        this.skew = 0.0
        this.fiducialId = pose.id
        this.bestCameraToTarget = Transform3d(Translation3d(
            pose.translationMeters_1[0],
            pose.translationMeters_1[1],
            pose.translationMeters_1[2]),
            Rotation3d(bestPoseQuat)
            )
        this.alternateCameraToTarget = Transform3d(
            Translation3d(
            pose.translationMeters_2[0],
            pose.translationMeters_2[1],
            pose.translationMeters_2[2]),
            Rotation3d(altPoseQuat)
        )
        val m_corners = mutableListOf<TargetCorner>()
        for (i in 0 until 8 step 2){
            m_corners.add(TargetCorner(pose.corners[i], pose.corners[i+1]))
        }
        this.corners = m_corners.toList()
        this.poseAmbiguity = pose.poseConfidence
        this.center = Pair(pose.center[0], pose.center[1])
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as PhotonTrackedTarget
        return (java.lang.Double.compare(that.yaw, yaw) == 0 && java.lang.Double.compare(
            that.pitch,
            pitch
        ) == 0 && java.lang.Double.compare(
            that.area,
            area
        ) == 0 && bestCameraToTarget == that.bestCameraToTarget
                && alternateCameraToTarget == that.alternateCameraToTarget
                && corners == that.corners)
    }

    override fun hashCode(): Int {
        return Objects.hash(yaw, pitch, area, bestCameraToTarget, alternateCameraToTarget)
    }

    /**
     * Populates the outgoing packet with information from the current target.
     *
     * @param packet The outgoing packet.
     * @return The outgoing packet.
     */
    fun populatePacket(packet: Packet): Packet {
        packet.encode(yaw)
        packet.encode(pitch)
        packet.encode(area)
        packet.encode(skew)
        packet.encode(fiducialId)
        encodeTransform(packet, bestCameraToTarget)
        encodeTransform(packet, alternateCameraToTarget)
        packet.encode(poseAmbiguity)
        for (i in 0..3) {
            packet.encode(corners[i].x)
            packet.encode(corners[i].y)
        }
        return packet
    }

    override fun toString(): String {
        return ("PhotonTrackedTarget{"
                + "yaw="
                + yaw
                + ", pitch="
                + pitch
                + ", area="
                + area
                + ", skew="
                + skew
                + ", fiducialId="
                + fiducialId
                + ", cameraToTarget="
                + bestCameraToTarget
                + ", targetCorners="
                + corners
                + '}')
    }

    companion object {
        const val PACK_SIZE_BYTES = java.lang.Double.BYTES * (5 + 7 + 2 * 4 + 1 + 7)

        private fun encodeTransform(packet: Packet, transform: Transform3d) {
            packet.encode(transform.translation.x) // x
            packet.encode(transform.translation.y) // y
            packet.encode(transform.translation.z) // z
            packet.encode(transform.rotation.quaternion.w)
            packet.encode(transform.rotation.quaternion.x)
            packet.encode(transform.rotation.quaternion.y)
            packet.encode(transform.rotation.quaternion.z)
        }
    }
}
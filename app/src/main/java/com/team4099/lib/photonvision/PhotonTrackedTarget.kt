package com.team4099.lib.photonvision

import com.team4099.lib.geo.rotationMatrixToQuaternion
import com.team4099.lib.geo.Transform3d
import com.team4099.lib.photonvision.networking.Packet
import edu.umich.eecs.april.apriltag.ApriltagPose
import java.util.*

class PhotonTrackedTarget {
    val yaw: Double

    val pitch: Double

    val area: Double

    val skew: Double

    /** Get the Fiducial ID, or -1 if not set.  */
    val fiducialId: Int

    /**
     * Get the transform that maps camera space (X = forward, Y = left, Z = up) to object/fiducial tag
     * space (X forward, Y left, Z up) with the lowest reprojection error
     */
    val bestCameraToTarget: Transform3d

    /**
     * Get the transform that maps camera space (X = forward, Y = left, Z = up) to object/fiducial tag
     * space (X forward, Y left, Z up) with the highest reprojection error
     */
    val alternateCameraToTarget: Transform3d

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
        val bestPoseQuat = rotationMatrixToQuaternion(pose.rotation_1)
        val altPoseQuat = rotationMatrixToQuaternion(pose.rotation_2)
        this.yaw = bestPoseQuat.yawRad
        this.pitch = bestPoseQuat.pitchRad
        this.area = 0.0
        this.skew = 0.0
        this.fiducialId = pose.id
        this.bestCameraToTarget = Transform3d(Triple(
            pose.translationMeters_1[0],
            pose.translationMeters_1[1],
            pose.translationMeters_1[2]),
            bestPoseQuat
            )
        this.alternateCameraToTarget = Transform3d(Triple(
            pose.translationMeters_2[0],
            pose.translationMeters_2[1],
            pose.translationMeters_2[2]),
            altPoseQuat
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

        //    private static Transform3d decodeTransform(Packet packet) {
        //        double x = packet.decodeDouble();
        //        double y = packet.decodeDouble();
        //        double z = packet.decodeDouble();
        //        Translation3d translation = new Translation3d(x, y, z);
        //        double w = packet.decodeDouble();
        //        x = packet.decodeDouble();
        //        y = packet.decodeDouble();
        //        z = packet.decodeDouble();
        //        Rotation3d rotation = new Rotation3d(new Quaternion(w, x, y, z));
        //        return new Transform3d(translation, rotation);
        //    }
        private fun encodeTransform(packet: Packet, transform: Transform3d) {
            packet.encode(transform.translation.first) // x
            packet.encode(transform.translation.second) // y
            packet.encode(transform.translation.third) // z
            packet.encode(transform.quat.w)
            packet.encode(transform.quat.x)
            packet.encode(transform.quat.y)
            packet.encode(transform.quat.z)
        }
    }
}
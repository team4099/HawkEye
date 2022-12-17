package com.team4099.lib.geo

import edu.wpi.first.math.MatBuilder
import edu.wpi.first.math.Nat
import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.util.Units
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MathUtils{
    companion object{
        private val APRILTAG_BASE_ROTATION =
            Rotation3d(VecBuilder.fill(1.0, 0.0, 0.0), Units.degreesToRadians(180.0))
        private val WPILIB_BASE_ROTATION = Rotation3d(
            MatBuilder(Nat.N3(), Nat.N3()).fill(
                0.0,
                1.0,
                0.0,
                0.0,
                0.0,
                1.0,
                1.0,
                0.0,
                0.0
            )
        )
        // AprilTags library reports EUN (X positive east, Y positive Up, Z positive North (outward))
        fun convertAprilTagOutputToEDN(pose: Transform3d): Transform3d{
            val ednRotation = APRILTAG_BASE_ROTATION.rotateBy(pose.rotation)
            return Transform3d(pose.translation, ednRotation)
        }

        // WPIlib uses NWU so we need to convert EUN AprilTags output to NWU
        fun convertEDNPoseToNWUPose(cameraToTarget3d: Transform3d): Pose3d{
            val nwu = CoordinateSystem.convert(Pose3d(cameraToTarget3d.translation, cameraToTarget3d.rotation), CoordinateSystem.EDN(), CoordinateSystem.NWU())
            return Pose3d(nwu.translation, WPILIB_BASE_ROTATION.rotateBy(nwu.rotation))
        }

        fun rotationMatrixToQuaternion(matrix: DoubleArray): Quaternion{
            val theta = acos((matrix[0] + matrix[4] + matrix[8] - 1)/2)
            var x = matrix[7] - matrix[5]
            var y = matrix[2] - matrix[6]
            var z = matrix[3] - matrix[1]
            val magnitude = sqrt(x*x + y*y + z*z)
            x /= magnitude
            y /= magnitude
            z /= magnitude
            return Quaternion(
                cos(theta/2), // w
                x * sin(theta/2), // x
                y * sin(theta/2), // y
                z * sin(theta/2), // z
            )
        }
    }
}
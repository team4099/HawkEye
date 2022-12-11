package com.team4099.lib.geo

data class Quaternion(val w: Double, val x: Double, val y: Double, val z: Double){

    // https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles#Quaternion_to_Euler_angles_conversion
    val rollRad: Double
        get() = Math.atan2(2.0 * (w * x + y * z), 1.0 - 2.0 * (x * x + y * y));

    val pitchRad
        get(): Double{
            val ratio = 2.0 * (w * y - z * x)
            if (Math.abs(ratio) >= 1.0) {
                return Math.copySign(Math.PI / 2.0, ratio)
            } else {
                return Math.asin(ratio)
            }
        }

    val yawRad: Double
        get() = Math.atan2(2.0 * (w * z + x * y), 1.0 - 2.0 * (y * y + z * z));

    fun getEulerAnglesRad(): Triple<Double, Double, Double> {
        return Triple(yawRad, pitchRad, rollRad);
    }

    fun getEulerAnglesDegrees(): Triple<Double, Double, Double> {
        return Triple(Math.toDegrees(yawRad), Math.toDegrees(pitchRad), Math.toDegrees(rollRad));
    }

}

fun rotationMatrixToQuaternion(R: DoubleArray): Quaternion{
    val q = DoubleArray(4)
    val m00 = R[0]
    val m10 = R[1]
    val m20 = R[2]
    val m01 = R[3]
    val m11 = R[4]
    val m21 = R[5]
    val m02 = R[6]
    val m12 = R[7]
    val m22 = R[8]
    val tr = m00 + m11 + m22
    if (tr > 0) {
        val S = Math.sqrt(tr + 1.0).toFloat() * 2 // S=4*qw
        q[0] = (0.25f * S).toDouble() /* w  w  w.j ava  2s.co m*/
        q[1] = (m21 - m12) / S
        q[2] = (m02 - m20) / S
        q[3] = (m10 - m01) / S
    } else if ((m00 > m11) and (m00 > m22)) {
        val S = Math.sqrt(1.0 + m00 - m11 - m22).toFloat() * 2 //
        // S=4*q[1]
        q[0] = (m21 - m12) / S
        q[1] = (0.25f * S).toDouble()
        q[2] = (m01 + m10) / S
        q[3] = (m02 + m20) / S
    } else if (m11 > m22) {
        val S = Math.sqrt(1.0 + m11 - m00 - m22).toFloat() * 2 //
        // S=4*q[2]
        q[0] = (m02 - m20) / S
        q[1] = (m01 + m10) / S
        q[2] = (0.25f * S).toDouble()
        q[3] = (m12 + m21) / S
    } else {
        val S = Math.sqrt(1.0 + m22 - m00 - m11).toFloat() * 2 //
        // S=4*q[3]
        q[0] = (m10 - m01) / S
        q[1] = (m02 + m20) / S
        q[2] = (m12 + m21) / S
        q[3] = (0.25f * S).toDouble()
    }
    return Quaternion(
        q[0], q[1], q[2], q[3]
    )
}
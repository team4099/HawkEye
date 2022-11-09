package com.team4099.lib

import edu.umich.eecs.april.apriltag.ApriltagPose

data class HawkeyeResult(val latencyMS: Double, val targets: List<ApriltagPose>){
    val hasTargets: Boolean = !(targets.isEmpty())

}

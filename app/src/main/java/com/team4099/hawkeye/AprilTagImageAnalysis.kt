package com.team4099.hawkeye

import android.annotation.SuppressLint
import android.hardware.camera2.CameraCharacteristics
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.ar.core.CameraIntrinsics
import edu.umich.eecs.april.apriltag.ApriltagPose

class AprilTagImageAnalysis: ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val yChannelValues = image.image?.planes?.get(0)?.buffer ?: return
        val timestamp = image.image?.timestamp?: return


    }
}
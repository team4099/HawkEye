/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.team4099.hawkeye.computervision

import android.util.Log
import com.google.ar.core.CameraIntrinsics
import com.team4099.lib.photonvision.networking.HawkeyeConfig
import edu.umich.eecs.april.apriltag.ApriltagNative
import edu.umich.eecs.april.apriltag.ApriltagPose
import kotlin.jvm.Synchronized
import java.nio.ByteBuffer

/** Detects edges from input YUV image.  */
class AprilTagDetector {
    private var inputPixels = ByteArray(0) // Reuse java byte array to avoid multiple allocations.

    init{
        ApriltagNative.apriltag_init(
            "tag16h5",
            0,
            2.0,
            0.0,
            6
        )
    }
    /**
     * Process a grayscale image using the Sobel edge detector.
     *
     * @param width image width.
     * @param height image height.
     * @param stride image stride (number of bytes per row, equals to width if no row padding).
     * @param input bytes of the image, assumed single channel grayscale of size [stride * height].
     * @return bytes of the processed image, where the byte value is the strength of the edge at that
     * pixel. Number of bytes is width * height, row padding (if any) is removed.
     */
    @Synchronized
    fun detect(width: Int, height: Int, stride: Int, input: ByteBuffer, intrinsics: CameraIntrinsics): Pair<ByteBuffer, List<ApriltagPose>> {
        // Reallocate input byte array if its size is different from the required size.
        if (stride * height > inputPixels.size) {
            inputPixels = ByteArray(stride * height)
        }

        // Allocate a new output byte array.
//        val outputPixels = ByteArray(width * height)

        // Copy input buffer into a java array for ease of access. This is not the most optimal
        // way to process an image, but used here for simplicity.
        input.position(0)

        // Note: On certain devices with specific resolution where the stride is not equal to the width.
        // In such situation the memory allocated for the frame may not be exact multiple of stride x
        // height hence the capacity of the ByteBuffer could be less. To handle such situations it will
        // be better to transfer the exact amount of image bytes to the destination bytes.
        input.get(inputPixels, 0, input.capacity())

        val poseOutput = ApriltagNative.getApriltagPoses(
            HawkeyeConfig.tagSizeMeters,
            inputPixels,
            width,
            height,
            intrinsics.focalLength[0].toDouble(), //fx
            intrinsics.focalLength[1].toDouble(), //fy
            intrinsics.principalPoint[0].toDouble(), //cx
            intrinsics.principalPoint[1].toDouble() //cy
        )
//        for (pose in poseOutput){
//            Log.i("apriltag_output", pose.toString())
//            println("++++++++")
//        }
//        println("----------")
//        // Detect edges.
        return Pair(ByteBuffer.wrap(inputPixels),poseOutput)

    }

    companion object {
        private const val SOBEL_EDGE_THRESHOLD = 128 * 128
    }
}
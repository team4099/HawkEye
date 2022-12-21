package com.team4099.lib.phonedata

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log

class CameraUtils(context: Context){
    private val cameraManager: CameraManager

    init{
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    @Throws(UnsupportedOperationException::class)
    fun obtainIntrinsicMatrix(): List<Float>{
        try{
//            for (id in cameraManager.cameraIdList){
//                val tempCharacteristics = cameraManager.getCameraCharacteristics(id)
//                val intrinisicMatrix = tempCharacteristics.get(CameraCharacteristics.LENS_INTRINSIC_CALIBRATION)
//                if (intrinisicMatrix != null) {
//                    for (bruh in intrinisicMatrix) {
//                        Log.i("CameraUtils", bruh.toString())
//                    }
//                }
//                Log.i("CameraUtils", "-------")
//            }

            // assuming the last ID is the highest resolution
            val cameraID = cameraManager.cameraIdList.last()

            val characteristics: CameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID)
            val intrinsicMatrix = mutableListOf<Float>()
            for (data in characteristics.get(CameraCharacteristics.LENS_INTRINSIC_CALIBRATION)!!){
                intrinsicMatrix.add(data)
            }
            return intrinsicMatrix.toList()

        } catch (e: Exception){
            throw UnsupportedOperationException("Intrinsic Matrix not obtained for your device")
        }
    }





}
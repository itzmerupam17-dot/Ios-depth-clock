package com.example.util

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build

object TorchHelper {
    private var isTorchOn = false

    fun isFlashlightOn(): Boolean = isTorchOn

    fun toggleTorch(context: Context): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull() ?: return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isTorchOn = !isTorchOn
                cameraManager.setTorchMode(cameraId, isTorchOn)
                isTorchOn
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isTorchOn = false
            false
        }
    }

    fun turnOffTorch(context: Context) {
        if (!isTorchOn) return
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull() ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false)
                isTorchOn = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.util.HapticHelper
import com.example.util.TorchHelper

@Composable
fun LockBottomActions(
    hapticEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isTorchActive by remember { mutableStateOf(TorchHelper.isFlashlightOn()) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 46.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flashlight Button (iOS circular glass button)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (isTorchActive) Color.White else Color.Black.copy(alpha = 0.45f)
                )
                .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                .clickable {
                    if (hapticEnabled) HapticHelper.playClick(context)
                    isTorchActive = TorchHelper.toggleTorch(context)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isTorchActive) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                contentDescription = "Toggle Torch",
                tint = if (isTorchActive) Color.Black else Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        // Camera Quick Launch Button
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f))
                .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                .clickable {
                    if (hapticEnabled) HapticHelper.playClick(context)
                    try {
                        val cameraIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(cameraIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Open Camera",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

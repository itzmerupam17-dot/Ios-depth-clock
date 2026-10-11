package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DepthLockConfig
import com.example.util.HapticHelper
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PinPadDialog(
    correctPin: String,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
    useBengali: Boolean = false,
    hapticEnabled: Boolean = true
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var enteredPin by remember { mutableStateOf("") }
    val shakeOffset = remember { Animatable(0f) }
    var isError by remember { mutableStateOf(false) }

    fun checkPin(pin: String) {
        if (pin.length == 4) {
            if (pin == correctPin || correctPin.isEmpty()) {
                if (hapticEnabled) HapticHelper.playClick(context)
                onSuccess()
            } else {
                isError = true
                if (hapticEnabled) HapticHelper.playError(context)
                scope.launch {
                    shakeOffset.animateTo(
                        targetValue = 0f,
                        animationSpec = keyframes {
                            durationMillis = 400
                            0f at 0
                            (-20f) at 50
                            20f at 100
                            (-15f) at 150
                            15f at 200
                            (-10f) at 250
                            10f at 300
                            0f at 400
                        }
                    )
                    enteredPin = ""
                    isError = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Text(
                text = if (useBengali) "পাসকোড দিন" else "Enter Passcode",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4 Dots
            Row(
                modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                repeat(4) { index ->
                    val filled = index < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isError -> Color.Red
                                    filled -> Color.White
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (isError) Color.Red else Color.White.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 3x4 iOS glass keypad
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "DEL")
            )

            rows.forEach { rowList ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowList.forEach { key ->
                        if (key.isEmpty()) {
                            Spacer(modifier = Modifier.size(72.dp))
                        } else if (key == "DEL") {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (enteredPin.isNotEmpty()) {
                                            if (hapticEnabled) HapticHelper.playClick(context)
                                            enteredPin = enteredPin.dropLast(1)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Delete",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        } else {
                            val displayDigit = if (useBengali) {
                                DepthLockConfig.convertToBengaliDigits(key)
                            } else {
                                key
                            }

                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                    .clickable {
                                        if (enteredPin.length < 4) {
                                            if (hapticEnabled) HapticHelper.playClick(context)
                                            val newPin = enteredPin + key
                                            enteredPin = newPin
                                            checkPin(newPin)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayDigit,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

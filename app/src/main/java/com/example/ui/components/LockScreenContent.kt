package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.model.DepthLockConfig
import com.example.model.SecurityMode
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LockScreenContent(
    config: DepthLockConfig,
    onDismissKeyguard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeString by remember { mutableStateOf("") }
    var currentDateString by remember { mutableStateOf("") }
    var showPinDialog by remember { mutableStateOf(false) }

    LaunchedEffect(config.is24Hour, config.showSeconds, config.useBengaliDigits) {
        val timePattern = when {
            config.is24Hour && config.showSeconds -> "HH:mm:ss"
            config.is24Hour -> "HH:mm"
            config.showSeconds -> "h:mm:ss"
            else -> "hh:mm"
        }
        val timeFormat = SimpleDateFormat(timePattern, Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

        while (true) {
            val now = Date()
            currentTimeString = timeFormat.format(now)
            currentDateString = dateFormat.format(now)
            delay(500)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // Double tap gesture
                    }
                )
            }
    ) {
        // 1. Depth Clock Layer (Wallpaper + Clock + Widgets + Depth Subject Cutout)
        DepthClockLayer(
            config = config,
            currentTimeString = currentTimeString.ifEmpty { "09:41" },
            currentDateString = currentDateString.ifEmpty { "Saturday, October 11" },
            modifier = Modifier.fillMaxSize(),
            isInteractivePreview = false
        )

        // 2. Bottom Quick Flashlight & Camera Actions
        LockBottomActions(
            hapticEnabled = config.hapticEnabled,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )

        // 3. Bottom Swipe-to-unlock Bar
        UnlockSlider(
            useBengali = config.useBengaliDigits,
            hapticEnabled = config.hapticEnabled,
            onUnlock = {
                if (config.securityMode == SecurityMode.PIN && config.pinCode.isNotEmpty()) {
                    showPinDialog = true
                } else {
                    onDismissKeyguard()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 14.dp)
        )

        // 4. PIN Dialog Overlay
        AnimatedVisibility(
            visible = showPinDialog,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PinPadDialog(
                correctPin = config.pinCode,
                useBengali = config.useBengaliDigits,
                hapticEnabled = config.hapticEnabled,
                onSuccess = {
                    showPinDialog = false
                    onDismissKeyguard()
                },
                onDismiss = {
                    showPinDialog = false
                }
            )
        }
    }
}

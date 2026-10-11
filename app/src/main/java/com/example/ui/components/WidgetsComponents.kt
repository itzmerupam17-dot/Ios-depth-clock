package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DepthLockConfig
import com.example.model.LockWidgetType

@Composable
fun LockWidgetsRack(
    widgets: List<LockWidgetType>,
    clockColor: Color,
    useBengaliDigits: Boolean,
    modifier: Modifier = Modifier,
    onWidgetClick: ((LockWidgetType) -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        widgets.take(4).forEach { widgetType ->
            LockWidgetTile(
                type = widgetType,
                clockColor = clockColor,
                useBengaliDigits = useBengaliDigits,
                onClick = { onWidgetClick?.invoke(widgetType) }
            )
        }
    }
}

@Composable
fun LockWidgetTile(
    type: LockWidgetType,
    clockColor: Color,
    useBengaliDigits: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var batteryPct by remember { mutableIntStateOf(85) }
    var isCharging by remember { mutableStateOf(false) }

    LaunchedEffect(type) {
        if (type == LockWidgetType.BATTERY) {
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
                context.registerReceiver(null, filter)
            }
            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            if (level != -1 && scale != -1) {
                batteryPct = (level * 100 / scale.toFloat()).toInt()
            }
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
        }
    }

    Box(
        modifier = Modifier
            .size(width = 68.dp, height = 52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black.copy(alpha = 0.28f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            LockWidgetType.BATTERY -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryFull,
                        contentDescription = "Battery",
                        tint = clockColor.copy(alpha = 0.95f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val pctStr = if (useBengaliDigits) {
                        "${DepthLockConfig.convertToBengaliDigits(batteryPct.toString())}%"
                    } else {
                        "$batteryPct%"
                    }
                    Text(
                        text = pctStr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = clockColor
                    )
                }
            }

            LockWidgetType.WEATHER -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Weather",
                        tint = Color(0xFFFFD166),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val tempStr = if (useBengaliDigits) {
                        "${DepthLockConfig.convertToBengaliDigits("26")}°"
                    } else {
                        "26°"
                    }
                    Text(
                        text = tempStr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = clockColor
                    )
                }
            }

            LockWidgetType.FITNESS -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    FitnessRingsIcon(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.height(2.dp))
                    val stepsStr = if (useBengaliDigits) {
                        DepthLockConfig.convertToBengaliDigits("7.4k")
                    } else {
                        "7.4k"
                    }
                    Text(
                        text = stepsStr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = clockColor
                    )
                }
            }

            LockWidgetType.CALENDAR -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = clockColor.copy(alpha = 0.95f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "2:00 PM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = clockColor
                    )
                }
            }

            LockWidgetType.TORCH -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashlightOn,
                        contentDescription = "Torch",
                        tint = clockColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Light",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = clockColor
                    )
                }
            }

            LockWidgetType.ALARM -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Alarm",
                        tint = clockColor.copy(alpha = 0.95f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val alarmStr = if (useBengaliDigits) {
                        DepthLockConfig.convertToBengaliDigits("07:30")
                    } else {
                        "07:30"
                    }
                    Text(
                        text = alarmStr,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = clockColor
                    )
                }
            }
        }
    }
}

@Composable
fun FitnessRingsIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.5f
        // Outer ring (Move - Pink/Red)
        drawArc(
            color = Color(0xFFFA2D48).copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(strokeWidth)
        )
        drawArc(
            color = Color(0xFFFA2D48),
            startAngle = -90f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )

        // Middle ring (Exercise - Green)
        drawArc(
            color = Color(0xFF92E3A9).copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(strokeWidth)
        )
        drawArc(
            color = Color(0xFF53D769),
            startAngle = -90f,
            sweepAngle = 210f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )

        // Inner ring (Stand - Cyan)
        drawArc(
            color = Color(0xFF00E5FF).copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(strokeWidth)
        )
        drawArc(
            color = Color(0xFF00E5FF),
            startAngle = -90f,
            sweepAngle = 310f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }
}

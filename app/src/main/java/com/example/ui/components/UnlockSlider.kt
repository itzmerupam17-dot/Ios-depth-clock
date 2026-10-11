package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.HapticHelper
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun UnlockSlider(
    onUnlock: () -> Unit,
    useBengali: Boolean = false,
    hapticEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dragOffset = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val chevronOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chevron_bounce"
    )

    val unlockThreshold = -120f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(0, dragOffset.value.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        // Only allow dragging upward (negative delta)
                        val newOffset = (dragOffset.value + delta).coerceIn(-200f, 0f)
                        dragOffset.snapTo(newOffset)
                    }
                },
                onDragStopped = {
                    if (dragOffset.value < unlockThreshold) {
                        if (hapticEnabled) HapticHelper.playHeavyClick(context)
                        onUnlock()
                        scope.launch { dragOffset.snapTo(0f) }
                    } else {
                        scope.launch {
                            dragOffset.animateTo(
                                0f,
                                animationSpec = tween(250, easing = FastOutSlowInEasing)
                            )
                        }
                    }
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bouncing chevron & text
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Swipe Up",
            tint = Color.White.copy(alpha = 0.75f),
            modifier = Modifier
                .size(24.dp)
                .offset(y = chevronOffset.dp)
        )

        val promptText = if (useBengali) {
            "আনলক করতে উপরে সোয়াইপ করুন"
        } else {
            "Swipe up to unlock"
        }

        Text(
            text = promptText,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // iOS Bottom Home Bar Indicator
        Box(
            modifier = Modifier
                .width(134.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.85f))
        )
    }
}

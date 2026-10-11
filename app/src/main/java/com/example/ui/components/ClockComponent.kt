package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClockFontStyle
import com.example.model.DepthLockConfig

/**
 * A large, clean, modern lock screen clock component inspired by iOS lock screen typography.
 * Features customizable fonts, soft ambient depth shadows, glow effects, and Bengali numeral support.
 */
@Composable
fun ClockComponent(
    time: String,
    modifier: Modifier = Modifier,
    dateText: String? = null,
    fontStyle: ClockFontStyle = ClockFontStyle.IOS_BOLD,
    color: Color = Color.White,
    fontSize: TextUnit = 88.sp,
    useBengaliDigits: Boolean = false,
    glowIntensity: Float = 0.4f,
    onClick: (() -> Unit)? = null
) {
    val formattedTime = remember(time, useBengaliDigits) {
        if (useBengaliDigits) {
            DepthLockConfig.convertToBengaliDigits(time)
        } else {
            time
        }
    }

    val formattedDate = remember(dateText, useBengaliDigits) {
        if (dateText != null && useBengaliDigits) {
            DepthLockConfig.convertToBengaliDigits(dateText)
        } else {
            dateText
        }
    }

    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(durationMillis = 250),
        label = "clock_color"
    )

    val (fontFamily, fontWeight, fontStyleSetting, letterSpacingVal) = when (fontStyle) {
        ClockFontStyle.IOS_BOLD -> Quadruple(FontFamily.SansSerif, FontWeight.ExtraBold, FontStyle.Normal, (-1.5).sp)
        ClockFontStyle.SF_ROUNDED -> Quadruple(FontFamily.SansSerif, FontWeight.Bold, FontStyle.Normal, (-0.5).sp)
        ClockFontStyle.NY_SERIF -> Quadruple(FontFamily.Serif, FontWeight.SemiBold, FontStyle.Normal, 0.sp)
        ClockFontStyle.MONO_TECH -> Quadruple(FontFamily.Monospace, FontWeight.Bold, FontStyle.Normal, 1.sp)
        ClockFontStyle.CALLIGRAPHY -> Quadruple(FontFamily.Cursive, FontWeight.Medium, FontStyle.Italic, 0.sp)
        ClockFontStyle.NEON_GLOW -> Quadruple(FontFamily.SansSerif, FontWeight.ExtraBold, FontStyle.Normal, (-1).sp)
        ClockFontStyle.MINIMAL -> Quadruple(FontFamily.SansSerif, FontWeight.Light, FontStyle.Normal, 1.5.sp)
    }

    val glowAlpha = (0.35f + glowIntensity * 0.45f).coerceIn(0f, 1f)
    val shadowOffset = if (fontStyle == ClockFontStyle.NEON_GLOW) Offset(0f, 0f) else Offset(0f, 4f)
    val blurRadius = if (fontStyle == ClockFontStyle.NEON_GLOW) 24f * glowIntensity else 12f

    Column(
        modifier = modifier
            .testTag("lock_clock_component")
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Optional top date label
        if (!formattedDate.isNullOrEmpty()) {
            Text(
                text = formattedDate,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.4.sp,
                color = animatedColor.copy(alpha = 0.92f),
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = Offset(0f, 2f),
                        blurRadius = 6f
                    )
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        // Main Large Clock Numerals
        Text(
            text = formattedTime,
            fontSize = fontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontStyle = fontStyleSetting,
            letterSpacing = letterSpacingVal,
            color = animatedColor,
            style = TextStyle(
                shadow = Shadow(
                    color = if (fontStyle == ClockFontStyle.NEON_GLOW) {
                        animatedColor.copy(alpha = glowAlpha)
                    } else {
                        Color.Black.copy(alpha = 0.55f)
                    },
                    offset = shadowOffset,
                    blurRadius = blurRadius
                )
            ),
            textAlign = TextAlign.Center
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ClockComponentPreview() {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ClockComponent(
            time = "09:41",
            dateText = "Saturday, October 11",
            fontStyle = ClockFontStyle.IOS_BOLD,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        ClockComponent(
            time = "০৯:৪১",
            dateText = "শনিবার, ১১ অক্টোবর",
            fontStyle = ClockFontStyle.SF_ROUNDED,
            color = Color(0xFF80D8FF),
            useBengaliDigits = true
        )
    }
}

package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.ClockFontStyle
import com.example.model.DepthLockConfig
import com.example.model.PresetWallpaper
import com.example.model.WallpaperCatalog

@Composable
fun DepthClockLayer(
    config: DepthLockConfig,
    currentTimeString: String,
    currentDateString: String,
    modifier: Modifier = Modifier,
    isInteractivePreview: Boolean = false,
    showXRayMask: Boolean = false,
    onWidgetClick: ((com.example.model.LockWidgetType) -> Unit)? = null
) {
    val preset = remember(config.selectedPresetId) {
        WallpaperCatalog.PRESETS.find { it.id == config.selectedPresetId }
            ?: WallpaperCatalog.PRESETS.first()
    }

    val clockColor = remember(config.clockColorArgb) {
        Color(config.clockColorArgb)
    }

    val formattedTime = remember(currentTimeString, config.useBengaliDigits) {
        if (config.useBengaliDigits) {
            DepthLockConfig.convertToBengaliDigits(currentTimeString)
        } else {
            currentTimeString
        }
    }

    val formattedDate = remember(currentDateString, config.useBengaliDigits) {
        if (config.useBengaliDigits) {
            DepthLockConfig.convertToBengaliDigits(currentDateString)
        } else {
            currentDateString
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val totalHeight = maxHeight
        val totalWidth = maxWidth

        // LAYER 1: BASE WALLPAPER (BOTTOM LAYER)
        WallpaperBackground(
            config = config,
            preset = preset,
            modifier = Modifier.fillMaxSize()
        )

        // Subtly dark gradient at top to make white text pop on bright backgrounds
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // LAYER 2: CLOCK & WIDGETS (MIDDLE LAYER)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = (85.dp + config.depthYOffsetDp.dp))
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Clock Component with customizable iOS modern font style and date
            ClockComponent(
                time = formattedTime,
                dateText = formattedDate,
                fontStyle = config.fontStyle,
                color = clockColor,
                fontSize = config.clockSizeSp.sp,
                useBengaliDigits = config.useBengaliDigits,
                glowIntensity = config.glowIntensity
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Below-Clock Widgets Rack
            if (config.activeWidgets.isNotEmpty()) {
                LockWidgetsRack(
                    widgets = config.activeWidgets,
                    clockColor = clockColor,
                    useBengaliDigits = config.useBengaliDigits,
                    onWidgetClick = onWidgetClick
                )
            }
        }

        // LAYER 3: FOREGROUND SUBJECT CUTOUT (TOP DEPTH LAYER)
        if (config.depthEffectEnabled) {
            val clipShape = remember(preset.id, config.depthOverlapRatio, config.customWallpaperUri) {
                SubjectDepthShape(
                    presetId = if (config.customWallpaperUri != null) "custom" else preset.id,
                    overlapRatio = config.depthOverlapRatio
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(clipShape)
            ) {
                WallpaperBackground(
                    config = config,
                    preset = preset,
                    modifier = Modifier.fillMaxSize()
                )

                if (showXRayMask) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Cyan.copy(alpha = 0.25f))
                            .border(2.dp, Color.Cyan)
                    )
                }
            }
        }
    }
}

@Composable
private fun WallpaperBackground(
    config: DepthLockConfig,
    preset: PresetWallpaper,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (config.customWallpaperUri != null) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(Uri.parse(config.customWallpaperUri))
                .crossfade(true)
                .build(),
            contentDescription = "Custom Wallpaper",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = preset.resId),
            contentDescription = preset.title,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

/**
 * Custom Shape that clips the foreground layer to carve out the subject (person's head, puppy ears, mountain ridge).
 * The upper boundary rises into the lower half of the clock digits according to [overlapRatio],
 * giving the signature iOS 16 depth wallpaper effect.
 */
class SubjectDepthShape(
    private val presetId: String,
    private val overlapRatio: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val width = size.width
        val height = size.height

        // Calculate the apex of the subject relative to screen height
        // Clock starts at ~85dp down, height ~100dp. Overlap apex should reach ~0.20 to 0.28 height!
        val baseOverlap = 0.24f - (overlapRatio * 0.08f) // between 0.18f and 0.24f
        val subjectTopY = (height * baseOverlap).coerceAtLeast(height * 0.16f)

        when (presetId) {
            "portrait_neon" -> {
                // Woman's head silhouette & stylish hair contour:
                // Hair crest peaks slightly to the right of center (~0.55 width)
                path.moveTo(0f, subjectTopY + height * 0.12f)
                // Left shoulder/hair slope up
                path.cubicTo(
                    width * 0.25f, subjectTopY + height * 0.08f,
                    width * 0.38f, subjectTopY + height * 0.01f,
                    width * 0.52f, subjectTopY // Peak of hair
                )
                // Right hair curve slope down over shoulder
                path.cubicTo(
                    width * 0.65f, subjectTopY + height * 0.01f,
                    width * 0.78f, subjectTopY + height * 0.06f,
                    width, subjectTopY + height * 0.10f
                )
                // Bottom of screen
                path.lineTo(width, height)
                path.lineTo(0f, height)
                path.close()
            }

            "golden_retriever" -> {
                // Dog's perked ears rising into clock:
                // Left ear peak at ~0.35 width, dip in middle head, right perked ear peak at ~0.72 width
                path.moveTo(0f, subjectTopY + height * 0.14f)
                // Rise to left ear
                path.cubicTo(
                    width * 0.15f, subjectTopY + height * 0.09f,
                    width * 0.26f, subjectTopY,
                    width * 0.34f, subjectTopY
                )
                // Dip between ears (head crown)
                path.cubicTo(
                    width * 0.42f, subjectTopY + height * 0.04f,
                    width * 0.50f, subjectTopY + height * 0.05f,
                    width * 0.58f, subjectTopY + height * 0.03f
                )
                // Rise to right perked ear
                path.cubicTo(
                    width * 0.66f, subjectTopY - height * 0.01f,
                    width * 0.74f, subjectTopY - height * 0.01f,
                    width * 0.82f, subjectTopY + height * 0.05f
                )
                // Right slope
                path.lineTo(width, subjectTopY + height * 0.13f)
                path.lineTo(width, height)
                path.lineTo(0f, height)
                path.close()
            }

            "alpine_sunset" -> {
                // Mountain summit ridge cutting across center
                path.moveTo(0f, subjectTopY + height * 0.10f)
                // Ascent along western ridge
                path.lineTo(width * 0.32f, subjectTopY + height * 0.05f)
                path.lineTo(width * 0.50f, subjectTopY) // Peak apex
                // Eastern slope descent
                path.lineTo(width * 0.70f, subjectTopY + height * 0.06f)
                path.lineTo(width, subjectTopY + height * 0.12f)
                path.lineTo(width, height)
                path.lineTo(0f, height)
                path.close()
            }

            else -> {
                // Universal / Custom image curved subject crest
                path.moveTo(0f, subjectTopY + height * 0.11f)
                path.cubicTo(
                    width * 0.30f, subjectTopY + height * 0.03f,
                    width * 0.45f, subjectTopY,
                    width * 0.50f, subjectTopY
                )
                path.cubicTo(
                    width * 0.55f, subjectTopY,
                    width * 0.70f, subjectTopY + height * 0.03f,
                    width, subjectTopY + height * 0.11f
                )
                path.lineTo(width, height)
                path.lineTo(0f, height)
                path.close()
            }
        }

        return Outline.Generic(path)
    }
}

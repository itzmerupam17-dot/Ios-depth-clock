package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

enum class ClockFontStyle(val displayName: String, val subtitle: String) {
    IOS_BOLD("iOS Bold", "Classic San Francisco heavy font"),
    SF_ROUNDED("SF Rounded", "Soft rounded iOS 16 aesthetic"),
    NY_SERIF("New York Serif", "Editorial elegance & serif style"),
    MONO_TECH("Cyber Mono", "Futuristic digital monospace"),
    CALLIGRAPHY("Handwritten", "Artistic fluid cursive script"),
    NEON_GLOW("Neon Tube", "Luminous glowing outlined numerals"),
    MINIMAL("Ultra Thin", "Razor-sharp minimalist look")
}

enum class LockWidgetType(val title: String, val defaultText: String) {
    BATTERY("Battery", "84%"),
    WEATHER("Weather", "26°C Sunny"),
    FITNESS("Activity Rings", "7,430 steps"),
    CALENDAR("Calendar", "Today 2:00 PM"),
    TORCH("Flashlight", "Ready"),
    ALARM("Alarm", "07:30 AM")
}

data class PresetWallpaper(
    val id: String,
    val title: String,
    val subtitle: String,
    @DrawableRes val resId: Int,
    val defaultClockColor: Long = 0xFFFFFFFF,
    val recommendedYOffset: Float = 0f,
    val depthOverlapPercent: Float = 0.35f,
    val isLightSubject: Boolean = false
)

object WallpaperCatalog {
    val PRESETS = listOf(
        PresetWallpaper(
            id = "portrait_neon",
            title = "Cyberpunk Violet",
            subtitle = "Portrait with vibrant hair overlapping clock digits",
            resId = R.drawable.wallpaper_portrait_1791700175982,
            defaultClockColor = 0xFFEDE7F6,
            recommendedYOffset = 30f,
            depthOverlapPercent = 0.40f
        ),
        PresetWallpaper(
            id = "golden_retriever",
            title = "Golden Puppy",
            subtitle = "Perked dog ears rising into clock area",
            resId = R.drawable.wallpaper_dog_1791700199258,
            defaultClockColor = 0xFFFFF8E1,
            recommendedYOffset = 25f,
            depthOverlapPercent = 0.38f
        ),
        PresetWallpaper(
            id = "alpine_sunset",
            title = "Alpine Summit",
            subtitle = "Snow-capped summit rising in twilight",
            resId = R.drawable.wallpaper_mountain_1791700153915,
            defaultClockColor = 0xFFFFFFFF,
            recommendedYOffset = 40f,
            depthOverlapPercent = 0.35f
        ),
        PresetWallpaper(
            id = "crystal_apex",
            title = "Crystal Pyramid",
            subtitle = "Luminous 3D crystal prism layering",
            resId = R.drawable.app_icon_art_1791700137441,
            defaultClockColor = 0xFFFFFFFF,
            recommendedYOffset = 10f,
            depthOverlapPercent = 0.45f
        )
    )
}

enum class SecurityMode(val title: String) {
    SWIPE("Swipe to Unlock"),
    PIN("4-Digit PIN Lock")
}

data class DepthLockConfig(
    val isServiceEnabled: Boolean = true,
    val depthEffectEnabled: Boolean = true,
    val depthOverlapRatio: Float = 0.35f, // percentage of clock tucked behind subject
    val depthYOffsetDp: Float = 20f, // vertical offset adjustment
    val fontStyle: ClockFontStyle = ClockFontStyle.IOS_BOLD,
    val clockColorArgb: Long = 0xFFFFFFFF,
    val clockSizeSp: Int = 86,
    val useBengaliDigits: Boolean = false,
    val is24Hour: Boolean = false,
    val showSeconds: Boolean = false,
    val aboveWidgetText: String = "Saturday, Oct 11",
    val aboveWidgetType: LockWidgetType = LockWidgetType.CALENDAR,
    val activeWidgets: List<LockWidgetType> = listOf(
        LockWidgetType.BATTERY,
        LockWidgetType.WEATHER,
        LockWidgetType.FITNESS,
        LockWidgetType.TORCH
    ),
    val selectedPresetId: String = "portrait_neon",
    val customWallpaperUri: String? = null,
    val securityMode: SecurityMode = SecurityMode.SWIPE,
    val pinCode: String = "",
    val hapticEnabled: Boolean = true,
    val doubleTapSleepEnabled: Boolean = true,
    val glowIntensity: Float = 0.4f
) {
    companion object {
        fun convertToBengaliDigits(input: String): String {
            val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
            val sb = java.lang.StringBuilder()
            for (ch in input) {
                if (ch in '0'..'9') {
                    sb.append(bengaliDigits[ch - '0'])
                } else {
                    sb.append(ch)
                }
            }
            return sb.toString()
        }
    }
}

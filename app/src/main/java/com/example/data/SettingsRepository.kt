package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ClockFontStyle
import com.example.model.DepthLockConfig
import com.example.model.LockWidgetType
import com.example.model.SecurityMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("depth_lock_prefs", Context.MODE_PRIVATE)

    private val _configFlow = MutableStateFlow(loadConfig())
    val configFlow: StateFlow<DepthLockConfig> = _configFlow.asStateFlow()

    fun getConfig(): DepthLockConfig = _configFlow.value

    private fun loadConfig(): DepthLockConfig {
        val fontIndex = prefs.getInt("font_style", 0)
        val font = ClockFontStyle.entries.getOrElse(fontIndex) { ClockFontStyle.IOS_BOLD }

        val secIndex = prefs.getInt("security_mode", 0)
        val secMode = SecurityMode.entries.getOrElse(secIndex) { SecurityMode.SWIPE }

        val widgetList = prefs.getString("active_widgets", "BATTERY,WEATHER,FITNESS,TORCH")
            ?.split(",")
            ?.mapNotNull { name ->
                try {
                    LockWidgetType.valueOf(name.trim())
                } catch (_: Exception) {
                    null
                }
            } ?: listOf(LockWidgetType.BATTERY, LockWidgetType.WEATHER)

        return DepthLockConfig(
            isServiceEnabled = prefs.getBoolean("service_enabled", true),
            depthEffectEnabled = prefs.getBoolean("depth_enabled", true),
            depthOverlapRatio = prefs.getFloat("depth_overlap", 0.35f),
            depthYOffsetDp = prefs.getFloat("depth_y_offset", 20f),
            fontStyle = font,
            clockColorArgb = prefs.getLong("clock_color", 0xFFFFFFFF),
            clockSizeSp = prefs.getInt("clock_size", 86),
            useBengaliDigits = prefs.getBoolean("use_bengali", false),
            is24Hour = prefs.getBoolean("is_24_hour", false),
            showSeconds = prefs.getBoolean("show_seconds", false),
            aboveWidgetText = prefs.getString("above_widget_text", "Saturday, Oct 11") ?: "Saturday, Oct 11",
            activeWidgets = widgetList,
            selectedPresetId = prefs.getString("selected_preset", "portrait_neon") ?: "portrait_neon",
            customWallpaperUri = prefs.getString("custom_wallpaper_uri", null),
            securityMode = secMode,
            pinCode = prefs.getString("pin_code", "") ?: "",
            hapticEnabled = prefs.getBoolean("haptic_enabled", true),
            doubleTapSleepEnabled = prefs.getBoolean("double_tap_sleep", true),
            glowIntensity = prefs.getFloat("glow_intensity", 0.4f)
        )
    }

    fun updateConfig(block: (DepthLockConfig) -> DepthLockConfig) {
        val current = _configFlow.value
        val updated = block(current)
        saveConfig(updated)
        _configFlow.value = updated
    }

    private fun saveConfig(c: DepthLockConfig) {
        prefs.edit().apply {
            putBoolean("service_enabled", c.isServiceEnabled)
            putBoolean("depth_enabled", c.depthEffectEnabled)
            putFloat("depth_overlap", c.depthOverlapRatio)
            putFloat("depth_y_offset", c.depthYOffsetDp)
            putInt("font_style", c.fontStyle.ordinal)
            putLong("clock_color", c.clockColorArgb)
            putInt("clock_size", c.clockSizeSp)
            putBoolean("use_bengali", c.useBengaliDigits)
            putBoolean("is_24_hour", c.is24Hour)
            putBoolean("show_seconds", c.showSeconds)
            putString("above_widget_text", c.aboveWidgetText)
            putString("active_widgets", c.activeWidgets.joinToString(",") { it.name })
            putString("selected_preset", c.selectedPresetId)
            putString("custom_wallpaper_uri", c.customWallpaperUri)
            putInt("security_mode", c.securityMode.ordinal)
            putString("pin_code", c.pinCode)
            putBoolean("haptic_enabled", c.hapticEnabled)
            putBoolean("double_tap_sleep", c.doubleTapSleepEnabled)
            putFloat("glow_intensity", c.glowIntensity)
            apply()
        }
    }
}

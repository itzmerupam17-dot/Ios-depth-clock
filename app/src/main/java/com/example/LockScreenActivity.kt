package com.example

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.SettingsRepository
import com.example.model.SecurityMode
import com.example.ui.components.LockScreenContent
import com.example.ui.theme.MyApplicationTheme

class LockScreenActivity : ComponentActivity() {

    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsRepository = SettingsRepository(this)

        configureLockScreenFlags()
        enableEdgeToEdge()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val config = settingsRepository.getConfig()
                if (config.securityMode == SecurityMode.PIN && config.pinCode.isNotEmpty()) {
                    // Do not allow back press when PIN is required
                } else {
                    finish()
                }
            }
        })

        setContent {
            MyApplicationTheme {
                val config by settingsRepository.configFlow.collectAsState()

                LockScreenContent(
                    config = config,
                    onDismissKeyguard = { dismissKeyguardAndExit() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    private fun configureLockScreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
    }

    private fun dismissKeyguardAndExit() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && keyguardManager != null) {
            keyguardManager.requestDismissKeyguard(this, null)
        }
        finish()
    }
}

package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SettingsRepository
import com.example.model.DepthLockConfig
import com.example.service.LockScreenService
import com.example.ui.components.DepthClockLayer
import com.example.ui.screens.ClockCustomizerScreen
import com.example.ui.screens.SystemClockGuideScreen
import com.example.ui.screens.WallpaperStudioScreen
import com.example.ui.screens.WidgetsScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        settingsRepository = SettingsRepository(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // Start service if enabled
        if (settingsRepository.getConfig().isServiceEnabled) {
            LockScreenService.start(this)
        }

        setContent {
            MyApplicationTheme {
                val config by settingsRepository.configFlow.collectAsState()

                MainScreen(
                    config = config,
                    onUpdateConfig = { block -> settingsRepository.updateConfig(block) },
                    onTestLockScreen = {
                        val intent = Intent(this, LockScreenActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

enum class NavigationTab(val title: String, val icon: ImageVector) {
    CLOCK("Clock", Icons.Default.AccessTime),
    WALLPAPER("Wallpapers", Icons.Default.Wallpaper),
    WIDGETS("Widgets", Icons.Default.Widgets),
    GUIDE("Lock Guide", Icons.Default.HelpOutline)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    config: DepthLockConfig,
    onUpdateConfig: ((DepthLockConfig) -> DepthLockConfig) -> Unit,
    onTestLockScreen: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.CLOCK) }
    var showXRayMask by remember { mutableStateOf(false) }

    var liveTimeString by remember { mutableStateOf("") }
    var liveDateString by remember { mutableStateOf("") }

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
            liveTimeString = timeFormat.format(now)
            liveDateString = dateFormat.format(now)
            delay(500)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DepthLock",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (config.isServiceEnabled) Color(0xFF00E676).copy(alpha = 0.2f)
                                    else Color.Gray.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (config.isServiceEnabled) "Active" else "Off",
                                color = if (config.isServiceEnabled) Color(0xFF00E676) else Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { onTestLockScreen() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Test Lock",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Test Lock",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Interactive Mini Lockscreen Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(22.dp))
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clickable { onTestLockScreen() }
            ) {
                DepthClockLayer(
                    config = config,
                    currentTimeString = liveTimeString.ifEmpty { "09:41" },
                    currentDateString = liveDateString.ifEmpty { "Saturday, October 11" },
                    showXRayMask = showXRayMask,
                    modifier = Modifier.fillMaxSize()
                )

                // "Tap to Test Fullscreen" pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Tap to Preview Fullscreen Lock Screen",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = selectedTab,
                    label = "tab_animation"
                ) { targetTab ->
                    when (targetTab) {
                        NavigationTab.CLOCK -> {
                            ClockCustomizerScreen(
                                config = config,
                                onUpdate = onUpdateConfig
                            )
                        }

                        NavigationTab.WALLPAPER -> {
                            WallpaperStudioScreen(
                                config = config,
                                showXRayMask = showXRayMask,
                                onToggleXRay = { showXRayMask = it },
                                onUpdate = onUpdateConfig
                            )
                        }

                        NavigationTab.WIDGETS -> {
                            WidgetsScreen(
                                config = config,
                                onUpdate = onUpdateConfig
                            )
                        }

                        NavigationTab.GUIDE -> {
                            SystemClockGuideScreen(
                                config = config,
                                onUpdate = onUpdateConfig
                            )
                        }
                    }
                }
            }
        }
    }
}

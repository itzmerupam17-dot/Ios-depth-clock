package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LockScreenActivity
import com.example.model.DepthLockConfig
import com.example.model.SecurityMode
import com.example.service.LockScreenService
import com.example.util.HapticHelper

@Composable
fun SystemClockGuideScreen(
    config: DepthLockConfig,
    onUpdate: ((DepthLockConfig) -> DepthLockConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card 1: Master Lock Screen Service Status
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (config.isServiceEnabled)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (config.isServiceEnabled) Color(0xFF00E676) else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DepthLock Service",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (config.isServiceEnabled) "Active • Overriding lock screen" else "Paused",
                                fontSize = 13.sp,
                                color = if (config.isServiceEnabled) Color(0xFF00E676) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = config.isServiceEnabled,
                        onCheckedChange = { isEnabled ->
                            if (config.hapticEnabled) HapticHelper.playClick(context)
                            onUpdate { it.copy(isServiceEnabled = isEnabled) }
                            if (isEnabled) {
                                LockScreenService.start(context)
                            } else {
                                LockScreenService.stop(context)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Test Lock Screen Button
                Button(
                    onClick = {
                        val intent = Intent(context, LockScreenActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Test",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test iOS Lock Screen Now")
                }
            }
        }

        // Card 2: HOW TO REMOVE DEFAULT MOBILE CLOCK (Main User Solution)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LockReset,
                        contentDescription = "Remove Default Clock",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ডিফল্ট লক স্ক্রিনের ঘড়ি সরান",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "How to remove the OEM stock lock screen clock",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Android সিস্টেমে ডিফল্ট ঘড়ি সরিয়ে পিওর iOS অভিজ্ঞতা পেতে নিচের পদক্ষেপগুলি অনুসরণ করুন:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Step 1
                GuideStepItem(
                    stepNumber = "১",
                    title = "সিস্টেম স্ক্রিন লক 'None' বা 'Swipe' করুন (সর্বোত্তম পদ্ধতি)",
                    description = "ফোনের Settings > Security/Lock screen এ গিয়ে Screen lock কে 'None' বা 'Swipe' নির্বাচন করুন। এতে ফোনের পুরনো লক স্ক্রিন ও ঘড়ি পুরোপুরি বন্ধ হয়ে যাবে এবং শুধু DepthLock এর iOS ঘড়ি প্রদর্শিত হবে!"
                )

                Spacer(modifier = Modifier.height(8.dp))

                FilledTonalButton(
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                context.startActivity(Intent(Settings.ACTION_SETTINGS))
                            } catch (_: Exception) {}
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open Settings",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ডিভাইস সিকিউরিটি সেটিংস খুলুন")
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Step 2: Samsung / Xiaomi specific tips
                GuideStepItem(
                    stepNumber = "২",
                    title = "Samsung One UI ব্যবহারকারীদের জন্য",
                    description = "Settings > Lock screen > Edit Lock screen এ ট্যাপ করে ঘড়ি উইজেট সিলেক্ট করুন এবং 'No Clock' বা ঘড়ি ডিলিট করুন।"
                )

                Spacer(modifier = Modifier.height(8.dp))

                GuideStepItem(
                    stepNumber = "৩",
                    title = "Xiaomi / Redmi / POCO HyperOS / MIUI ব্যবহারকারীদের জন্য",
                    description = "Settings > Always-on display & Lock screen > Lock screen clock format এ গিয়ে ঘড়ি বন্ধ বা মিনিমালিস্ট করুন।"
                )

                Spacer(modifier = Modifier.height(8.dp))

                GuideStepItem(
                    stepNumber = "৪",
                    title = "Google Pixel / Motorola / Stock Android এর জন্য",
                    description = "Settings > Display > Lock screen এ গিয়ে 'Double-line clock' বন্ধ করুন অথবা সিস্টেম লক 'None' রাখুন।"
                )
            }
        }

        // Card 3: DepthLock Security & Passcode
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "DepthLock Security Mode",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose how to unlock DepthLock",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecurityMode.entries.forEach { mode ->
                        val isSelected = config.securityMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (config.hapticEnabled) HapticHelper.playClick(context)
                                    onUpdate { it.copy(securityMode = mode) }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (config.securityMode == SecurityMode.PIN) {
                    Spacer(modifier = Modifier.height(14.dp))
                    var enteredPin by remember { mutableStateOf(config.pinCode) }

                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                enteredPin = it
                                onUpdate { cfg -> cfg.copy(pinCode = it) }
                            }
                        },
                        label = { Text("Set 4-Digit Passcode (PIN)") },
                        placeholder = { Text("e.g. 1234") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Haptic feedback toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Haptics",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Haptic Vibration Feedback",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Switch(
                        checked = config.hapticEnabled,
                        onCheckedChange = { isChecked ->
                            onUpdate { it.copy(hapticEnabled = isChecked) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideStepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }
    }
}

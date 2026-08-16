package com.ekshana.tv.launcher.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.data.AppInfo
import com.ekshana.tv.launcher.ui.theme.*

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    rawApps: List<AppInfo>,
    hiddenApps: Set<String>,
    onToggleHide: (String) -> Unit,
    onUnhideAll: () -> Unit,
    onClearFavorites: () -> Unit,
    onCleanRam: () -> Unit,
    onShowInputSwitcher: () -> Unit,
    onBack: () -> Unit,
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isDefaultLauncher by remember { mutableStateOf(false) }
    var showHiddenAppsList by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }

    fun checkDefault() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val info = context.packageManager
            .resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
        isDefaultLauncher = info?.activityInfo?.packageName == context.packageName
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) checkDefault()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun launch(intent: Intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            try {
                context.startActivity(
                    Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } catch (_: Exception) { /* ignore */ }
        }
    }

    fun triggerDefaultLauncherFlow() {
        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            val chooser = Intent.createChooser(homeIntent, "Select Home App").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            return
        } catch (_: Exception) { /* fall through */ }

        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(homeIntent)
            return
        } catch (_: Exception) { /* fall through */ }

        launch(Intent(Settings.ACTION_SETTINGS))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 44.dp, end = 44.dp, top = 20.dp, bottom = 44.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Zen Launcher Settings",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusTextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Configure launcher behavior & quick system shortcuts",
                            fontSize = 11.5.sp,
                            color = StatusIconColor
                        )
                    }
                    SettingsNavButton(text = "✕ Back", onClick = onBack)
                }
            }

            // Quick Actions Card
            item {
                SettingsSectionCard(title = "⚡ Performance & Actions") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingsActionButton(
                            title = "Free Memory",
                            subtitle = "Clean background tasks",
                            modifier = Modifier.weight(1f),
                            onClick = onCleanRam
                        )
                        SettingsActionButton(
                            title = "TV Inputs",
                            subtitle = "HDMI 1/2/3, AV, Cable",
                            modifier = Modifier.weight(1f),
                            onClick = onShowInputSwitcher
                        )
                        SettingsActionButton(
                            title = if (isDefaultLauncher) "✓ Default Launcher" else "★ Set as Default",
                            subtitle = if (isDefaultLauncher) "Currently active" else "Replace stock launcher",
                            modifier = Modifier.weight(1f),
                            onClick = { triggerDefaultLauncherFlow() }
                        )
                    }
                }
            }

            // Android System Shortcuts
            item {
                SettingsSectionCard(title = "⚙️ System Settings Shortcuts") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingsActionButton(
                            title = "Wi-Fi & Network",
                            subtitle = "Wireless & Ethernet",
                            modifier = Modifier.weight(1f),
                            onClick = { launch(Intent(Settings.ACTION_WIFI_SETTINGS)) }
                        )
                        SettingsActionButton(
                            title = "Bluetooth Remotes",
                            subtitle = "Pair remotes & audio",
                            modifier = Modifier.weight(1f),
                            onClick = { launch(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) }
                        )
                        SettingsActionButton(
                            title = "All TV Settings",
                            subtitle = "Full Android settings",
                            modifier = Modifier.weight(1f),
                            onClick = { launch(Intent(Settings.ACTION_SETTINGS)) }
                        )
                    }
                }
            }

            // Launcher App Management
            item {
                SettingsSectionCard(title = "📦 Launcher Management") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingsActionButton(
                            title = if (showHiddenAppsList) "Hide Management" else "Manage Hidden Apps",
                            subtitle = "${hiddenApps.size} hidden",
                            modifier = Modifier.weight(1f),
                            onClick = { showHiddenAppsList = !showHiddenAppsList }
                        )
                        SettingsActionButton(
                            title = "Clear Favorites",
                            subtitle = if (showClearConfirm) "Click again to confirm" else "Reset favorite pins",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (showClearConfirm) {
                                    onClearFavorites()
                                    showClearConfirm = false
                                } else {
                                    showClearConfirm = true
                                }
                            }
                        )
                    }
                }
            }

            // Hidden Apps List
            if (showHiddenAppsList) {
                if (hiddenApps.isEmpty()) {
                    item {
                        Text(
                            text = "No apps are hidden. Long-press any app on the home screen to hide it.",
                            fontSize = 12.sp,
                            color = StatusIconColor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hidden Apps (${hiddenApps.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusTextPrimary
                            )
                            SettingsNavButton(text = "Unhide All", onClick = onUnhideAll)
                        }
                    }
                    val hiddenList = rawApps.filter { hiddenApps.contains(it.packageName) }
                    items(hiddenList.size) { index ->
                        val app = hiddenList[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E2530))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = app.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                                Text(
                                    text = app.packageName,
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            SettingsNavButton(text = "Unhide", onClick = { onToggleHide(app.packageName) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(TopGlassContainerBg)
            .padding(14.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = StatusTextPrimary,
            letterSpacing = 0.5.sp
        )
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsActionButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(14.dp)),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, Color(0x20FFFFFF)),
                shape = RoundedCornerShape(14.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(14.dp)
            )
        ),
        colors = ButtonDefaults.colors(
            containerColor = Color(0xFF1E2530),
            focusedContainerColor = Color(0xFF2E384A)
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsNavButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, Color(0x20FFFFFF)),
                shape = RoundedCornerShape(10.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(10.dp)
            )
        ),
        colors = ButtonDefaults.colors(
            containerColor = Color(0xFF1E2530),
            focusedContainerColor = Color(0xFF2E384A)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

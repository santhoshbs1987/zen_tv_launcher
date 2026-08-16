package com.ekshana.tv.launcher.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.ekshana.tv.launcher.R
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
            contentPadding = PaddingValues(start = 44.dp, end = 44.dp, top = 22.dp, bottom = 44.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            text = "Settings",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusTextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "System shortcuts & launcher preferences",
                            fontSize = 11.5.sp,
                            color = StatusTextSecondary
                        )
                    }
                    SettingsNavButton(text = "✕  Back to Home", onClick = onBack)
                }
            }

            // Quick Actions Card
            item {
                SettingsSectionCard(title = "⚡ Performance & Actions") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsActionButton(
                            title = "Free Memory",
                            subtitle = "Clean background tasks",
                            icon = "⚡",
                            modifier = Modifier.weight(1f),
                            onClick = onCleanRam
                        )
                        SettingsActionButton(
                            title = "TV Inputs",
                            subtitle = "HDMI 1/2/3, AV, Cable",
                            icon = "🔌",
                            modifier = Modifier.weight(1f),
                            onClick = onShowInputSwitcher
                        )
                        SettingsActionButton(
                            title = if (isDefaultLauncher) "Default Home Active" else "Set Default Home",
                            subtitle = if (isDefaultLauncher) "Currently active" else "Replace stock launcher",
                            icon = if (isDefaultLauncher) "✓" else "★",
                            modifier = Modifier.weight(1f),
                            onClick = { triggerDefaultLauncherFlow() }
                        )
                    }
                }
            }

            // Android System Shortcuts
            item {
                SettingsSectionCard(title = "⚙️ System Preferences") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsActionButton(
                            title = "Wi-Fi & Network",
                            subtitle = "Wireless & Ethernet",
                            icon = "📶",
                            modifier = Modifier.weight(1f),
                            onClick = { launch(Intent(Settings.ACTION_WIFI_SETTINGS)) }
                        )
                        SettingsActionButton(
                            title = "Bluetooth Remotes",
                            subtitle = "Pair remotes & audio",
                            icon = "🎧",
                            modifier = Modifier.weight(1f),
                            onClick = { launch(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) }
                        )
                        SettingsActionButton(
                            title = "All TV Settings",
                            subtitle = "Full Android settings",
                            icon = "⚙️",
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsActionButton(
                            title = if (showHiddenAppsList) "Close Hidden List" else "Manage Hidden Apps",
                            subtitle = "${hiddenApps.size} hidden apps",
                            icon = "👁",
                            modifier = Modifier.weight(1f),
                            onClick = { showHiddenAppsList = !showHiddenAppsList }
                        )
                        SettingsActionButton(
                            title = if (showClearConfirm) "Click to Confirm" else "Clear Favorites",
                            subtitle = if (showClearConfirm) "Reset favorite list" else "Reset favorite pins",
                            icon = "↺",
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
                            color = StatusTextSecondary,
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
                                .clip(RoundedCornerShape(14.dp))
                                .background(ModalGlassBg)
                                .border(BorderStroke(1.dp, ModalGlassBorder), RoundedCornerShape(14.dp))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = app.label,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = StatusTextPrimary
                                )
                                Text(
                                    text = app.packageName,
                                    fontSize = 10.sp,
                                    color = StatusTextSecondary
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(TopGlassContainerBg)
            .border(BorderStroke(1.dp, TopGlassContainerBorder), RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = StatusTextPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(12.dp))
            content()
        }

        // Top Specular Sheen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .align(Alignment.TopCenter)
                .background(ModalSheenGradient)
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsActionButton(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.04f else 1.0f,
        animationSpec = tween(150),
        label = "settingsActionScale"
    )

    Button(
        onClick = onClick,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(14.dp)),
        scale = ButtonDefaults.scale(scale = 1.0f, focusedScale = 1.0f),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, ButtonGlassBorder),
                shape = RoundedCornerShape(14.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, ButtonGlassFocusedBorder),
                shape = RoundedCornerShape(14.dp)
            )
        ),
        colors = ButtonDefaults.colors(
            containerColor = ButtonGlassBg,
            focusedContainerColor = ButtonGlassFocusedBg
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        modifier = modifier
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .then(
                if (isFocused) {
                    Modifier.shadow(12.dp, RoundedCornerShape(14.dp), ambientColor = Color.Black)
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = icon,
                fontSize = 17.sp,
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = if (isFocused) FontWeight.Bold else FontWeight.SemiBold,
                    color = StatusTextPrimary,
                    maxLines = 1
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    color = StatusTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsNavButton(
    text: String,
    onClick: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = tween(150),
        label = "navButtonScale"
    )

    Button(
        onClick = onClick,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
        scale = ButtonDefaults.scale(scale = 1.0f, focusedScale = 1.0f),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, ButtonGlassBorder),
                shape = RoundedCornerShape(10.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, ButtonGlassFocusedBorder),
                shape = RoundedCornerShape(10.dp)
            )
        ),
        colors = ButtonDefaults.colors(
            containerColor = ButtonGlassBg,
            focusedContainerColor = ButtonGlassFocusedBg
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
        modifier = Modifier
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
    ) {
        Text(
            text = text,
            fontSize = 11.5.sp,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
            color = StatusTextPrimary
        )
    }
}

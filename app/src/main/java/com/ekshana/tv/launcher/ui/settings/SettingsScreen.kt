package com.ekshana.tv.launcher.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.ekshana.tv.launcher.ui.theme.AccentCyan
import com.ekshana.tv.launcher.ui.theme.BackgroundGradient
import com.ekshana.tv.launcher.ui.theme.CardBg
import com.ekshana.tv.launcher.ui.theme.CardBorderIdle
import com.ekshana.tv.launcher.ui.theme.CardFocusedBg
import com.ekshana.tv.launcher.ui.theme.DialogBg
import com.ekshana.tv.launcher.ui.theme.DialogSurface
import com.ekshana.tv.launcher.ui.theme.FocusBorderColor
import com.ekshana.tv.launcher.ui.theme.TextMuted
import com.ekshana.tv.launcher.ui.theme.TextPrimary
import com.ekshana.tv.launcher.ui.theme.TextSecondary

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

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
                            text = "Zen TV Settings",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Configure launcher behavior & quick system shortcuts",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                    }
                    SettingsActionButton(
                        text = "←  Back to Home",
                        textColor = AccentCyan,
                        onClick = onBack
                    )
                }
                Spacer(Modifier.height(10.dp))
                Divider()
            }

            // 1. Default Launcher
            item {
                SectionLabel("DEFAULT LAUNCHER")
                Spacer(Modifier.height(6.dp))
                SettingsRow(
                    title = if (isDefaultLauncher) "✓  Zen TV is your default home"
                    else "Zen TV is NOT set as default",
                    subtitle = if (isDefaultLauncher)
                        "Press Home on your remote to land here directly."
                    else
                        "Tap \"Choose Home App\", then select Zen TV → \"Always\".",
                    statusColor = if (isDefaultLauncher) Color(0xFF4ADE80) else Color(0xFFFBBF24),
                ) {
                    if (!isDefaultLauncher) {
                        SettingsActionButton(text = "Choose Home App", onClick = { triggerDefaultLauncherFlow() })
                    }
                }
            }

            // 2. TV Inputs
            item {
                SectionLabel("TV INPUTS & SOURCES")
                Spacer(Modifier.height(6.dp))
                SettingsRow(
                    title = "🔌  Switch TV Source",
                    subtitle = "HDMI 1 (ARC), HDMI 2, HDMI 3, AV (Composite), Antenna",
                ) {
                    SettingsActionButton(text = "Select Input", onClick = onShowInputSwitcher)
                }
            }

            // 3. Performance
            item {
                SectionLabel("PERFORMANCE")
                Spacer(Modifier.height(6.dp))
                SettingsRow(
                    title = "⚡  One-Tap RAM Cleaner",
                    subtitle = "Free up memory immediately before launching heavy streaming apps",
                ) {
                    SettingsActionButton(text = "Clean RAM Now", textColor = AccentCyan, onClick = onCleanRam)
                }
            }

            // 4. App Visibility
            item {
                SectionLabel("APP VISIBILITY")
                Spacer(Modifier.height(6.dp))
                SettingsRow(
                    title = "Hidden Apps (${hiddenApps.size} hidden)",
                    subtitle = if (hiddenApps.isEmpty()) "No apps are hidden from the launcher grid"
                    else "Toggle visibility for installed or system apps",
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (hiddenApps.isNotEmpty()) {
                            SettingsActionButton(text = "Unhide All", onClick = onUnhideAll)
                        }
                        SettingsActionButton(
                            text = if (showHiddenAppsList) "Hide List ▲" else "Manage Apps ▼",
                            onClick = { showHiddenAppsList = !showHiddenAppsList }
                        )
                    }
                }
            }

            if (showHiddenAppsList) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DialogBg)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Select an app to toggle visibility on Home Screen:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        rawApps.forEach { app ->
                            val isHidden = hiddenApps.contains(app.packageName)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = app.label,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isHidden) TextMuted else TextPrimary
                                    )
                                    Text(
                                        text = if (isHidden) "Hidden from grid" else "Visible",
                                        fontSize = 11.sp,
                                        color = if (isHidden) Color(0xFFF87171) else Color(0xFF4ADE80)
                                    )
                                }
                                SettingsActionButton(
                                    text = if (isHidden) "Unhide" else "Hide",
                                    onClick = { onToggleHide(app.packageName) }
                                )
                            }
                        }
                    }
                }
            }

            // 5. System Shortcuts
            item {
                SectionLabel("SYSTEM SHORTCUTS")
                Spacer(Modifier.height(6.dp))
                SettingsRow(
                    title = "Android TV Settings",
                    subtitle = "Open device settings, preferences, accounts",
                ) {
                    SettingsActionButton(text = "Open", onClick = { launch(Intent(Settings.ACTION_SETTINGS)) })
                }
            }

            item {
                SettingsRow(
                    title = "Network & Internet",
                    subtitle = "Wi-Fi, Ethernet, IP settings",
                ) {
                    SettingsActionButton(text = "Open", onClick = { launch(Intent(Settings.ACTION_WIRELESS_SETTINGS)) })
                }
            }

            item {
                SettingsRow(
                    title = "Manage Installed Apps",
                    subtitle = "App permissions, storage, clear app cache",
                ) {
                    SettingsActionButton(text = "Open", onClick = { launch(Intent(Settings.ACTION_APPLICATION_SETTINGS)) })
                }
            }

            item {
                SettingsRow(
                    title = "Display & Sound",
                    subtitle = "Picture mode, resolution, screen saver",
                ) {
                    SettingsActionButton(text = "Open", onClick = { launch(Intent(Settings.ACTION_DISPLAY_SETTINGS)) })
                }
            }

            // 6. Favorites
            item {
                SectionLabel("FAVORITES")
                Spacer(Modifier.height(6.dp))
                SettingsRow(
                    title = "Clear All Favorites",
                    subtitle = "Remove all pinned apps from the Favorites row",
                ) {
                    if (showClearConfirm) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SettingsActionButton(text = "Confirm", textColor = Color(0xFFF87171), onClick = {
                                onClearFavorites()
                                showClearConfirm = false
                            })
                            SettingsActionButton(text = "Cancel", onClick = { showClearConfirm = false })
                        }
                    } else {
                        SettingsActionButton(text = "Clear Favorites", onClick = { showClearConfirm = true })
                    }
                }
            }

            // 7. About
            item {
                SectionLabel("ABOUT")
                Spacer(Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DialogBg)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AboutRow(label = "App", value = "Zen TV")
                    AboutRow(label = "Version", value = "1.0")
                    AboutRow(label = "Platform", value = "Mi LED Smart TV 4A (32\") · Android TV 9")
                    AboutRow(label = "Architecture", value = "Ultra-lightweight ~29 MB memory footprint")
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Modern Private helper composables
// -------------------------------------------------------------------------

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        color = AccentCyan,
        letterSpacing = 1.2.sp,
    )
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF1F283C).copy(alpha = 0.6f)),
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    statusColor: Color = TextPrimary,
    action: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DialogBg)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
            Spacer(Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.5.sp, color = TextSecondary)
        }
        Spacer(Modifier.width(20.dp))
        action()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsActionButton(
    text: String,
    onClick: () -> Unit,
    textColor: Color = TextPrimary,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, CardBorderIdle),
                shape = RoundedCornerShape(10.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, FocusBorderColor),
                shape = RoundedCornerShape(10.dp)
            )
        ),
        colors = ButtonDefaults.colors(
            containerColor = CardBg,
            focusedContainerColor = CardFocusedBg
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = label, fontSize = 12.5.sp, color = TextSecondary, modifier = Modifier.width(110.dp))
        Text(text = value, fontSize = 12.5.sp, color = TextPrimary)
    }
}

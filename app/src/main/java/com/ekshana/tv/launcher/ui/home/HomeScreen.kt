package com.ekshana.tv.launcher.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.data.AppInfo
import com.ekshana.tv.launcher.data.TvInputManagerHelper
import com.ekshana.tv.launcher.ui.components.AllAppsGrid
import com.ekshana.tv.launcher.ui.components.AppContextMenu
import com.ekshana.tv.launcher.ui.components.FavoritesRow
import com.ekshana.tv.launcher.ui.components.TvInputSwitcherDialog
import com.ekshana.tv.launcher.ui.settings.SettingsScreen
import com.ekshana.tv.launcher.ui.theme.AccentCyan
import com.ekshana.tv.launcher.ui.theme.BackgroundGradient
import com.ekshana.tv.launcher.ui.theme.CardBg
import com.ekshana.tv.launcher.ui.theme.CardBorderIdle
import com.ekshana.tv.launcher.ui.theme.CardFocusedBg
import com.ekshana.tv.launcher.ui.theme.DialogBg
import com.ekshana.tv.launcher.ui.theme.FocusBorderColor
import com.ekshana.tv.launcher.ui.theme.TextMuted
import com.ekshana.tv.launcher.ui.theme.TextPrimary
import com.ekshana.tv.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    menuPressedTrigger: Long = 0L,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lastFocusedPackage by viewModel.lastFocusedPackage.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedContextApp by remember { mutableStateOf<AppInfo?>(null) }
    var focusedAppLabel by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var showInputSwitcher by remember { mutableStateOf(false) }

    val focusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }

    LaunchedEffect(uiState.rawApps) {
        uiState.rawApps.forEach { app ->
            if (!focusRequesters.containsKey(app.packageName)) {
                focusRequesters[app.packageName] = FocusRequester()
            }
        }
    }

    LaunchedEffect(menuPressedTrigger) {
        if (menuPressedTrigger > 0L) {
            showSettings = !showSettings
        }
    }

    var hasRestoredFocus by remember { mutableStateOf(false) }
    LaunchedEffect(lastFocusedPackage, showSettings, selectedContextApp, showInputSwitcher) {
        val isOverlayOpen = showSettings || selectedContextApp != null || showInputSwitcher
        if (!isOverlayOpen && lastFocusedPackage != null && !hasRestoredFocus) {
            delay(120)
            try {
                focusRequesters[lastFocusedPackage]?.requestFocus()
                hasRestoredFocus = true
            } catch (_: Exception) { /* ignore */ }
        }
    }

    LaunchedEffect(focusedAppLabel) {
        if (focusedAppLabel != null) {
            hasRestoredFocus = true
        }
    }

    val isAnyOverlayOpen = showSettings || selectedContextApp != null || showInputSwitcher
    BackHandler(enabled = !isAnyOverlayOpen) { /* no-op */ }

    if (showSettings) {
        SettingsScreen(
            rawApps = uiState.rawApps,
            hiddenApps = uiState.hiddenApps,
            onToggleHide = { pkg -> viewModel.toggleHideApp(pkg) },
            onUnhideAll = { viewModel.unhideAllApps() },
            onClearFavorites = { viewModel.clearFavorites() },
            onCleanRam = { viewModel.cleanRam(context) },
            onShowInputSwitcher = { showInputSwitcher = true },
            onBack = {
                showSettings = false
                hasRestoredFocus = false
            },
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient),
    ) {
        // ---- PREMIUM LOADING SPLASH SCREEN ----
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(DialogBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 36.sp,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Zen TV",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Starting up…",
                        fontSize = 12.5.sp,
                        color = AccentCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // ---- MAIN HOME UI ----
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar(
                    focusedAppLabel = focusedAppLabel,
                    onInputsClick = { showInputSwitcher = true },
                    onCleanRamClick = { viewModel.cleanRam(context) },
                    onSettingsClick = { showSettings = true }
                )

                if (uiState.favorites.isNotEmpty()) {
                    FavoritesRow(
                        favorites = uiState.favorites,
                        focusRequesters = focusRequesters,
                        onAppClick = {
                            hasRestoredFocus = false
                            viewModel.launchApp(context, it.packageName)
                        },
                        onAppLongClick = { app -> selectedContextApp = app },
                        onAppFocused = { app ->
                            focusedAppLabel = app.label
                            viewModel.setLastFocusedPackage(app.packageName)
                        },
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }

                AllAppsGrid(
                    apps = uiState.allApps,
                    focusRequesters = focusRequesters,
                    onAppClick = {
                        hasRestoredFocus = false
                        viewModel.launchApp(context, it.packageName)
                    },
                    onAppLongClick = { app -> selectedContextApp = app },
                    onAppFocused = { app ->
                        focusedAppLabel = app.label
                        viewModel.setLastFocusedPackage(app.packageName)
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // In-Hierarchy Modal Overlay: Context Menu
        selectedContextApp?.let { app ->
            AppContextMenu(
                app = app,
                isFavorite = viewModel.isFavorite(app.packageName),
                onToggleFavorite = { viewModel.toggleFavorite(app.packageName) },
                onMoveFavoriteLeft = { viewModel.moveFavorite(app.packageName, -1) },
                onMoveFavoriteRight = { viewModel.moveFavorite(app.packageName, 1) },
                onToggleHide = { viewModel.toggleHideApp(app.packageName) },
                onAppInfo = { viewModel.openAppInfo(context, app.packageName) },
                onUninstall = { viewModel.uninstallApp(context, app.packageName) },
                onDismiss = { selectedContextApp = null },
            )
        }

        // In-Hierarchy Modal Overlay: TV Inputs Switcher
        if (showInputSwitcher) {
            TvInputSwitcherDialog(
                onSelectInput = { inputItem ->
                    TvInputManagerHelper.switchInput(context, inputItem)
                },
                onDismiss = { showInputSwitcher = false }
            )
        }
    }
}

// -------------------------------------------------------------------------
// Ultra-Modern Glassmorphic TopBar (Clock at the End)
// -------------------------------------------------------------------------

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TopBar(
    focusedAppLabel: String?,
    onInputsClick: () -> Unit,
    onCleanRamClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    var timeStr by remember { mutableStateOf(formattedTime()) }
    var dateStr by remember { mutableStateOf(formattedDate()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            timeStr = formattedTime()
            dateStr = formattedDate()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 44.dp, top = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: App Title with Accent Pill & Ambient Focus Tracker
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AccentCyan)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Zen TV",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.4.sp,
            )

            if (focusedAppLabel != null) {
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(TextMuted)
                )
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DialogBg)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = focusedAppLabel,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentCyan,
                        maxLines = 1,
                    )
                }
            }
        }

        // Right: Action Buttons followed by Clock at the very end
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TopBarButton(
                text = "🔌  Inputs",
                textColor = TextPrimary,
                onClick = onInputsClick
            )

            TopBarButton(
                text = "⚡  Clean RAM",
                textColor = AccentCyan,
                onClick = onCleanRamClick
            )

            TopBarButton(
                text = "⚙  Settings",
                textColor = TextPrimary,
                onClick = onSettingsClick
            )

            Spacer(Modifier.width(4.dp))

            // Minimalist Clock placed at the very end
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = timeStr,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 0.4.sp,
                )
                Text(
                    text = dateStr,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TopBarButton(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
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
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun formattedTime(): String =
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

private fun formattedDate(): String =
    SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())

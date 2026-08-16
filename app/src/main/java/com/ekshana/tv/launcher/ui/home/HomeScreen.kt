package com.ekshana.tv.launcher.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.ekshana.tv.launcher.R
import com.ekshana.tv.launcher.data.AppInfo
import com.ekshana.tv.launcher.data.TvInputManagerHelper
import com.ekshana.tv.launcher.ui.components.AllAppsGrid
import com.ekshana.tv.launcher.ui.components.AppContextMenu
import com.ekshana.tv.launcher.ui.components.TvInputSwitcherDialog
import com.ekshana.tv.launcher.ui.settings.SettingsScreen
import com.ekshana.tv.launcher.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    menuPressedTrigger: Long = 0L,
    inputPressedTrigger: Long = 0L,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lastFocusedPackage by viewModel.lastFocusedPackage.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedContextApp by remember { mutableStateOf<AppInfo?>(null) }
    var focusedAppLabel by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var showInputSwitcher by remember { mutableStateOf(false) }

    val gridFocusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }

    // Prepend Inputs, Settings, and Free Memory tiles in the first row
    val tvInputApp = remember {
        AppInfo(
            label = "Inputs",
            packageName = "com.ekshana.tv.launcher.inputs",
            iconBitmap = null
        )
    }

    val androidSettingsApp = remember {
        AppInfo(
            label = "Settings",
            packageName = "com.ekshana.tv.launcher.settings",
            iconBitmap = null
        )
    }

    val freeMemoryApp = remember {
        AppInfo(
            label = "Free Memory",
            packageName = "com.ekshana.tv.launcher.ramcleaner",
            iconBitmap = null
        )
    }

    val displayApps = remember(uiState.allApps) {
        listOf(tvInputApp, androidSettingsApp, freeMemoryApp) + uiState.allApps.filter {
            it.packageName != tvInputApp.packageName &&
            it.packageName != androidSettingsApp.packageName &&
            it.packageName != freeMemoryApp.packageName
        }
    }

    LaunchedEffect(displayApps) {
        displayApps.forEach { app ->
            if (!gridFocusRequesters.containsKey(app.packageName)) {
                gridFocusRequesters[app.packageName] = FocusRequester()
            }
        }
    }

    LaunchedEffect(menuPressedTrigger) {
        if (menuPressedTrigger > 0L) {
            showSettings = !showSettings
        }
    }

    LaunchedEffect(inputPressedTrigger) {
        if (inputPressedTrigger > 0L) {
            showInputSwitcher = !showInputSwitcher
        }
    }

    LaunchedEffect(showSettings, selectedContextApp, showInputSwitcher, uiState.isLoading) {
        val isOverlayOpen = showSettings || selectedContextApp != null || showInputSwitcher
        if (!isOverlayOpen && !uiState.isLoading) {
            delay(150)
            try {
                val targetPkg = lastFocusedPackage
                    ?: displayApps.firstOrNull()?.packageName
                if (targetPkg != null) {
                    gridFocusRequesters[targetPkg]?.requestFocus()
                }
            } catch (_: Exception) { /* ignore */ }
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
            },
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient),
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Zen Launcher",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Status Bar (24-hour clock, Wi-Fi, Settings, Inputs)
                TopStatusBar(
                    onInputsClick = { showInputSwitcher = true },
                    onSettingsClick = { showSettings = true }
                )

                Spacer(Modifier.height(14.dp))

                // Unified All Apps Grid (with Inputs, Settings, and Free Memory in first positions)
                AllAppsGrid(
                    apps = displayApps,
                    focusRequesters = gridFocusRequesters,
                    onAppClick = { app ->
                        when (app.packageName) {
                            tvInputApp.packageName -> {
                                showInputSwitcher = true
                            }
                            androidSettingsApp.packageName -> {
                                try {
                                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (_: ActivityNotFoundException) {
                                    showSettings = true
                                }
                            }
                            freeMemoryApp.packageName -> {
                                viewModel.cleanRam(context)
                            }
                            else -> {
                                viewModel.launchApp(context, app.packageName)
                            }
                        }
                    },
                    onAppLongClick = { app ->
                        when (app.packageName) {
                            tvInputApp.packageName -> showInputSwitcher = true
                            androidSettingsApp.packageName -> showSettings = true
                            freeMemoryApp.packageName -> viewModel.cleanRam(context)
                            else -> selectedContextApp = app
                        }
                    },
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

// -----------------------------------------------------------------------------
// Top Status Bar with crisp vector icons (Wi-Fi, Gear, Sliders)
// -----------------------------------------------------------------------------

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TopStatusBar(
    onInputsClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    var timeStr by remember { mutableStateOf(formatted24hTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000L)
            timeStr = formatted24hTime()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 44.dp, top = 22.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 24h Clock (e.g. 22:57)
        Text(
            text = timeStr,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = StatusTextPrimary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.width(18.dp))

        // Wi-Fi Icon
        Image(
            painter = painterResource(R.drawable.ic_wifi),
            contentDescription = "Wi-Fi",
            colorFilter = ColorFilter.tint(StatusIconColor),
            modifier = Modifier.size(19.dp)
        )

        Spacer(Modifier.width(14.dp))

        // Settings Icon Button
        StatusIconButton(
            onClick = onSettingsClick,
            content = { isFocused ->
                Image(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = "Settings",
                    colorFilter = ColorFilter.tint(if (isFocused) StatusIconActive else StatusIconColor),
                    modifier = Modifier.size(19.dp)
                )
            }
        )

        Spacer(Modifier.width(10.dp))

        // TV Inputs / Slider Controls Icon Button
        StatusIconButton(
            onClick = onInputsClick,
            content = { isFocused ->
                Image(
                    painter = painterResource(R.drawable.ic_tune),
                    contentDescription = "Inputs",
                    colorFilter = ColorFilter.tint(if (isFocused) StatusIconActive else StatusIconColor),
                    modifier = Modifier.size(19.dp)
                )
            }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun StatusIconButton(
    onClick: () -> Unit,
    content: @Composable (isFocused: Boolean) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    Button(
        onClick = onClick,
        shape = ButtonDefaults.shape(shape = CircleShape),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(0.dp, Color.Transparent),
                shape = CircleShape
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, Color.White),
                shape = CircleShape
            )
        ),
        colors = ButtonDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color(0x35FFFFFF)
        ),
        contentPadding = PaddingValues(5.dp),
        modifier = Modifier
            .size(32.dp)
            .onFocusChanged { isFocused = it.isFocused }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            content(isFocused)
        }
    }
}

private fun formatted24hTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

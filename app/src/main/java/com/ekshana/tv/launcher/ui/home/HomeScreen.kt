package com.ekshana.tv.launcher.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
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
import com.ekshana.tv.launcher.ui.components.RecommendationsRow
import com.ekshana.tv.launcher.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    inputPressedTrigger: Long = 0L,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lastFocusedPackage by viewModel.lastFocusedPackage.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedContextApp by remember { mutableStateOf<AppInfo?>(null) }
    var showHiddenAppsModal by remember { mutableStateOf(false) }
    var reorderingPackage by remember { mutableStateOf<String?>(null) }
    var focusedAppLabel by remember { mutableStateOf<String?>(null) }

    val gridFocusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }

    val displayApps = uiState.allApps
    val hiddenAppsList = remember(uiState.rawApps, uiState.hiddenApps) {
        uiState.rawApps.filter { uiState.hiddenApps.contains(it.packageName) }
    }

    LaunchedEffect(displayApps) {
        displayApps.forEach { app ->
            if (!gridFocusRequesters.containsKey(app.packageName)) {
                gridFocusRequesters[app.packageName] = FocusRequester()
            }
        }
    }

    LaunchedEffect(inputPressedTrigger) {
        if (inputPressedTrigger > 0L) {
            TvInputManagerHelper.openNativeInputsMenu(context)
        }
    }

    LaunchedEffect(selectedContextApp, reorderingPackage, uiState.isLoading) {
        val isOverlayOpen = selectedContextApp != null || reorderingPackage != null
        if (!isOverlayOpen && !uiState.isLoading) {
            delay(150)
            try {
                val targetPkg = lastFocusedPackage ?: displayApps.firstOrNull()?.packageName
                if (targetPkg != null) {
                    gridFocusRequesters[targetPkg]?.requestFocus()
                }
            } catch (_: Exception) { /* ignore */ }
        }
    }

    val isAnyOverlayOpen = selectedContextApp != null || reorderingPackage != null
    BackHandler(enabled = isAnyOverlayOpen) {
        if (reorderingPackage != null) {
            reorderingPackage = null
        } else if (selectedContextApp != null) {
            selectedContextApp = null
        }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRestorer()
            ) {
                // Top Status Bar (Interactive 24-hour clock, Wi-Fi, System Settings, Inputs, Hidden Apps)
                TopStatusBar(
                    hiddenCount = hiddenAppsList.size,
                    onHiddenAppsClick = { showHiddenAppsModal = true },
                    onClockClick = { viewModel.openDateSettings(context) },
                    onWifiClick = { viewModel.openWifiSettings(context) },
                    onSettingsClick = { viewModel.openSystemSettings(context) },
                    onInputsClick = { TvInputManagerHelper.openNativeInputsMenu(context) }
                )

                Spacer(Modifier.height(10.dp))

                // Watch Next / OS TV Recommendations (TvContractCompat)
                if (uiState.recommendations.isNotEmpty()) {
                    RecommendationsRow(
                        recommendations = uiState.recommendations,
                        onRecommendationClick = { rec ->
                            viewModel.launchRecommendation(context, rec)
                        }
                    )
                }

                // All Installed TV Apps Grid
                AllAppsGrid(
                    apps = displayApps,
                    focusRequesters = gridFocusRequesters,
                    reorderingPackage = reorderingPackage,
                    onAppClick = { app ->
                        if (reorderingPackage != null) {
                            reorderingPackage = null
                        } else {
                            viewModel.launchApp(context, app.packageName)
                        }
                    },
                    onAppLongClick = { app ->
                        if (reorderingPackage == null) {
                            selectedContextApp = app
                        }
                    },
                    onAppFocused = { app ->
                        focusedAppLabel = app.label
                        viewModel.setLastFocusedPackage(app.packageName)
                    },
                    onMoveApp = { pkg, delta ->
                        viewModel.moveAppByPackage(pkg, delta)
                    },
                    onFinishReordering = {
                        reorderingPackage = null
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Reorder Mode floating HUD banner
        if (reorderingPackage != null) {
            val appInfo = displayApps.find { it.packageName == reorderingPackage }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xE6141824), RoundedCornerShape(20.dp))
                        .border(BorderStroke(1.5.dp, Color(0xFF00E5FF)), RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "⇄ Moving: ${appInfo?.label ?: "App"}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "• D-pad [← → ↑ ↓] to Move   • [OK / BACK] to Done",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = StatusTextPrimary
                        )
                    }
                }
            }
        }

        // In-Hierarchy Modal Overlay: Context Menu (Hide, Rearrange, App Info, Uninstall)
        selectedContextApp?.let { app ->
            AppContextMenu(
                app = app,
                onStartReorder = {
                    viewModel.setLastFocusedPackage(app.packageName)
                    reorderingPackage = app.packageName
                },
                onToggleHide = { viewModel.toggleHideApp(app.packageName) },
                onAppInfo = { viewModel.openAppInfo(context, app.packageName) },
                onUninstall = { viewModel.uninstallApp(context, app.packageName) },
                onDismiss = { selectedContextApp = null },
            )
        }

        // In-Hierarchy Modal Overlay: Hidden Apps Manager
        if (showHiddenAppsModal && hiddenAppsList.isNotEmpty()) {
            com.ekshana.tv.launcher.ui.components.HiddenAppsModal(
                hiddenApps = hiddenAppsList,
                onUnhideApp = { app -> viewModel.toggleHideApp(app.packageName) },
                onUnhideAll = { viewModel.unhideAllApps() },
                onDismiss = { showHiddenAppsModal = false }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Top Status Bar with Native OS Panel triggers
// -----------------------------------------------------------------------------

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TopStatusBar(
    hiddenCount: Int,
    onHiddenAppsClick: () -> Unit,
    onClockClick: () -> Unit,
    onWifiClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onInputsClick: () -> Unit,
) {
    val context = LocalContext.current
    var timeStr by remember { mutableStateOf(getSystemFormattedTime(context)) }

    LaunchedEffect(context) {
        while (true) {
            delay(15_000L)
            timeStr = getSystemFormattedTime(context)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 44.dp, top = 20.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (hiddenCount > 0) {
            StatusTextButton(
                text = "👁 Hidden ($hiddenCount)",
                onClick = onHiddenAppsClick
            )
            Spacer(Modifier.width(12.dp))
        }

        // Interactive System Clock (Opens native OS Date & Time panel)
        StatusTextButton(
            text = timeStr,
            onClick = onClockClick
        )

        Spacer(Modifier.width(14.dp))

        // Wi-Fi Icon Button (Opens native OS Network / Wi-Fi panel)
        StatusIconButton(
            onClick = onWifiClick,
            content = { isFocused ->
                Image(
                    painter = painterResource(R.drawable.ic_wifi),
                    contentDescription = "Wi-Fi Settings",
                    colorFilter = ColorFilter.tint(if (isFocused) StatusIconActive else StatusIconColor),
                    modifier = Modifier.size(19.dp)
                )
            }
        )

        Spacer(Modifier.width(10.dp))

        // Settings Icon Button (Opens native OS System Settings panel)
        StatusIconButton(
            onClick = onSettingsClick,
            content = { isFocused ->
                Image(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = "System Settings",
                    colorFilter = ColorFilter.tint(if (isFocused) StatusIconActive else StatusIconColor),
                    modifier = Modifier.size(19.dp)
                )
            }
        )

        Spacer(Modifier.width(10.dp))

        // TV Inputs Side Menu Button (Opens native Android TV Inputs side panel)
        StatusIconButton(
            onClick = onInputsClick,
            content = { isFocused ->
                Image(
                    painter = painterResource(R.drawable.ic_tune),
                    contentDescription = "TV Inputs",
                    colorFilter = ColorFilter.tint(if (isFocused) StatusIconActive else StatusIconColor),
                    modifier = Modifier.size(19.dp)
                )
            }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun StatusTextButton(
    text: String,
    onClick: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    Button(
        onClick = onClick,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(12.dp)),
        border = ButtonDefaults.border(
            border = Border(border = BorderStroke(0.dp, Color.Transparent), shape = RoundedCornerShape(12.dp)),
            focusedBorder = Border(border = BorderStroke(2.dp, Color.White), shape = RoundedCornerShape(12.dp))
        ),
        colors = ButtonDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color(0x35FFFFFF)
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = Modifier.onFocusChanged { isFocused = it.isFocused }
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = if (isFocused) Color.White else StatusTextPrimary,
            letterSpacing = 0.5.sp,
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

private fun getSystemFormattedTime(context: android.content.Context): String =
    android.text.format.DateFormat.getTimeFormat(context).format(Date())


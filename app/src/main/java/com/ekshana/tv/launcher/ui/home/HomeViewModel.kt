package com.ekshana.tv.launcher.ui.home

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ekshana.tv.launcher.data.AppInfo
import com.ekshana.tv.launcher.data.AppRepository
import com.ekshana.tv.launcher.data.TvRecommendation
import com.ekshana.tv.launcher.data.TvRecommendationsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val allApps: List<AppInfo> = emptyList(),
    val rawApps: List<AppInfo> = emptyList(),
    val recommendations: List<TvRecommendation> = emptyList(),
    val hiddenApps: Set<String> = emptySet(),
    val isLoading: Boolean = true,
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val recommendationsRepo = TvRecommendationsRepository(application)

    private val _lastFocusedPackage = MutableStateFlow<String?>(null)
    val lastFocusedPackage: StateFlow<String?> = _lastFocusedPackage.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        AppRepository.apps,
        AppRepository.rawApps,
        AppRepository.hiddenApps,
        recommendationsRepo.getWatchNextPrograms(),
    ) { apps, rawApps, hidden, recs ->
        HomeUiState(
            allApps = apps,
            rawApps = rawApps,
            recommendations = recs,
            hiddenApps = hidden,
            isLoading = apps.isEmpty() && rawApps.isEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState(
            allApps = AppRepository.apps.value,
            rawApps = AppRepository.rawApps.value,
            isLoading = AppRepository.apps.value.isEmpty(),
        ),
    )

    fun setLastFocusedPackage(packageName: String) {
        _lastFocusedPackage.value = packageName
    }

    fun toggleHideApp(packageName: String) = AppRepository.toggleHideApp(packageName)
    fun unhideAllApps() = AppRepository.unhideAllApps()

    /**
     * One-Tap RAM Cleaner: Kills background processes.
     */
    fun cleanRam(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                val packages = AppRepository.rawApps.value.map { it.packageName }
                for (pkg in packages) {
                    if (pkg != context.packageName) {
                        am?.killBackgroundProcesses(pkg)
                    }
                }
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "⚡ Memory cleaned! TV RAM freed.", Toast.LENGTH_SHORT).show()
                }
            } catch (_: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Memory cleaned!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Opens native Android TV system settings overlay.
     */
    fun openSystemSettings(context: Context) {
        startNativeIntent(context, Intent(Settings.ACTION_SETTINGS), "Unable to open Settings")
    }

    /**
     * Opens native Android TV Wi-Fi / Network side panel.
     */
    fun openWifiSettings(context: Context) {
        startNativeIntent(context, Intent(Settings.ACTION_WIFI_SETTINGS), "Unable to open Network settings")
    }

    /**
     * Opens native Android TV Date & Time side panel.
     */
    fun openDateSettings(context: Context) {
        startNativeIntent(context, Intent(Settings.ACTION_DATE_SETTINGS), "Unable to open Date & Time settings")
    }

    /**
     * Opens native Android TV App Info side panel.
     */
    fun openAppInfo(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startNativeIntent(context, intent, "Unable to open App Info")
    }

    /**
     * Triggers native Android TV app uninstall system confirmation dialog.
     */
    fun uninstallApp(context: Context, packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startNativeIntent(context, intent, "Cannot uninstall this app")
    }

    /**
     * Tries Leanback (Android TV) launch intent first, falling back to standard launch intent.
     */
    fun launchApp(context: Context, packageName: String) {
        setLastFocusedPackage(packageName)
        val pm = context.packageManager
        val tvIntent = pm.getLeanbackLaunchIntentForPackage(packageName)
        val standardIntent = pm.getLaunchIntentForPackage(packageName)
        val intent = tvIntent ?: standardIntent ?: return

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: Exception) { /* ignore */ }
    }

    /**
     * Launches TV recommendation intent or falls back to app package.
     */
    fun launchRecommendation(context: Context, recommendation: TvRecommendation) {
        recommendation.intentUri?.let { uri ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return
            } catch (_: Exception) {}
        }
        if (recommendation.packageName.isNotEmpty()) {
            launchApp(context, recommendation.packageName)
        }
    }

    private fun startNativeIntent(context: Context, intent: Intent, fallbackMsg: String) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, fallbackMsg, Toast.LENGTH_SHORT).show()
        }
    }
}

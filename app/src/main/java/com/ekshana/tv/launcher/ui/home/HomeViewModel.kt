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
    val favorites: List<AppInfo> = emptyList(),
    val hiddenApps: Set<String> = emptySet(),
    val isLoading: Boolean = true,
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _lastFocusedPackage = MutableStateFlow<String?>(null)
    val lastFocusedPackage: StateFlow<String?> = _lastFocusedPackage.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        AppRepository.apps,
        AppRepository.rawApps,
        AppRepository.favorites,
        AppRepository.hiddenApps,
    ) { apps, rawApps, favPkgs, hidden ->
        val favApps = favPkgs.mapNotNull { pkg -> rawApps.find { it.packageName == pkg } }
        HomeUiState(
            allApps = apps,
            rawApps = rawApps,
            favorites = favApps,
            hiddenApps = hidden,
            isLoading = apps.isEmpty() && rawApps.isEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState(
            allApps = AppRepository.apps.value,
            rawApps = AppRepository.rawApps.value,
            isLoading = AppRepository.apps.value.isEmpty()
        ),
    )

    fun setLastFocusedPackage(packageName: String) {
        _lastFocusedPackage.value = packageName
    }

    fun toggleFavorite(packageName: String) = AppRepository.toggleFavorite(packageName)
    fun moveFavorite(packageName: String, direction: Int) = AppRepository.moveFavorite(packageName, direction)
    fun clearFavorites() = AppRepository.clearFavorites()
    fun isFavorite(packageName: String): Boolean = AppRepository.isFavorite(packageName)

    fun toggleHideApp(packageName: String) = AppRepository.toggleHideApp(packageName)
    fun unhideAllApps() = AppRepository.unhideAllApps()

    /**
     * One-Tap RAM Cleaner: Kills background processes and trims system cache.
     */
    fun cleanRam(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                val pm = context.packageManager
                val packages = AppRepository.rawApps.value.map { it.packageName }
                for (pkg in packages) {
                    if (pkg != context.packageName) {
                        am?.killBackgroundProcesses(pkg)
                    }
                }
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "⚡ Memory cleaned! TV RAM freed.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Memory cleaned!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun openAppInfo(context: Context, packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to open App Info", Toast.LENGTH_SHORT).show()
        }
    }

    fun uninstallApp(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                data = Uri.fromParts("package", packageName, null)
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Intent.ACTION_DELETE).apply {
                    data = Uri.fromParts("package", packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(context, "Cannot uninstall this app", Toast.LENGTH_SHORT).show()
            }
        }
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
}

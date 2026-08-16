package com.ekshana.tv.launcher.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Singleton app repository.
 *
 * Fast icon & Leanback TV banner pipeline:
 * - Checks ri.activityInfo.loadBanner(pm) or ri.activityInfo.applicationInfo.loadBanner(pm) first for official 16:9 / horizontal TV banners
 * - Falls back to ri.loadIcon(pm) if no banner exists
 * - Caches decoded ImageBitmaps so cards render instantly
 */
object AppRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Bitmap cache by packageName
    private val iconCache = ConcurrentHashMap<String, ImageBitmap>()

    private val _rawApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val rawApps: StateFlow<List<AppInfo>> = _rawApps.asStateFlow()

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    /** Ordered list of package names that the user has pinned as favourites. */
    private val _favorites = MutableStateFlow<List<String>>(emptyList())
    val favorites: StateFlow<List<String>> = _favorites.asStateFlow()

    /** Set of package names hidden by the user. */
    private val _hiddenApps = MutableStateFlow<Set<String>>(emptySet())
    val hiddenApps: StateFlow<Set<String>> = _hiddenApps.asStateFlow()

    private lateinit var prefs: SharedPreferences
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadPreferences()
        refresh()
    }

    private fun loadPreferences() {
        val savedFavs = prefs.getString(KEY_FAVORITES_ORDERED, null)
        if (savedFavs != null && savedFavs.isNotEmpty()) {
            _favorites.value = savedFavs.split(",").filter { it.isNotEmpty() }
        } else {
            val legacy = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
            _favorites.value = legacy.toList()
        }

        val savedHidden = prefs.getStringSet(KEY_HIDDEN, emptySet()) ?: emptySet()
        _hiddenApps.value = savedHidden
    }

    private fun saveFavorites(favs: List<String>) {
        _favorites.value = favs
        prefs.edit()
            .putString(KEY_FAVORITES_ORDERED, favs.joinToString(","))
            .putStringSet(KEY_FAVORITES, favs.toSet())
            .apply()
    }

    private fun saveHidden(hidden: Set<String>) {
        _hiddenApps.value = hidden
        prefs.edit().putStringSet(KEY_HIDDEN, hidden).apply()
        applyFilter()
    }

    /**
     * Queries PackageManager, decodes Leanback TV Banners or Icons, caches them, and updates StateFlow.
     */
    fun refresh() {
        scope.launch {
            val pm = appContext.packageManager

            // 1. Android TV apps (Prime Video, Hotstar, Netflix, YouTube, etc.)
            val tvIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
            }
            @Suppress("DEPRECATION")
            val tvApps = pm.queryIntentActivities(tvIntent, 0)

            // 2. Standard / Mobile / Sideloaded apps
            val standardIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            @Suppress("DEPRECATION")
            val standardApps = pm.queryIntentActivities(standardIntent, 0)

            val combinedResolves = (tvApps + standardApps)
                .filter { it.activityInfo.packageName != appContext.packageName }
                .distinctBy { it.activityInfo.packageName }

            val appList = combinedResolves.map { ri ->
                val pkg = ri.activityInfo.packageName
                val cached = iconCache[pkg]
                val bitmap = if (cached != null) {
                    cached
                } else {
                    try {
                        // Prioritize Leanback Banner (16:9 / horizontal TV banner)
                        val bannerDrawable = try {
                            ri.activityInfo.loadBanner(pm)
                                ?: ri.activityInfo.applicationInfo.loadBanner(pm)
                        } catch (_: Exception) {
                            null
                        }

                        if (bannerDrawable != null) {
                            val bmp = bannerDrawable.toBitmap(width = 240, height = 135, config = Bitmap.Config.ARGB_8888)
                            val imgBmp = bmp.asImageBitmap()
                            iconCache[pkg] = imgBmp
                            imgBmp
                        } else {
                            val drawable = ri.loadIcon(pm)
                            val bmp = drawable.toBitmap(width = 120, height = 120, config = Bitmap.Config.ARGB_8888)
                            val imgBmp = bmp.asImageBitmap()
                            iconCache[pkg] = imgBmp
                            imgBmp
                        }
                    } catch (_: Exception) {
                        null
                    }
                }
                AppInfo(
                    label = ri.loadLabel(pm).toString(),
                    packageName = pkg,
                    iconBitmap = bitmap,
                )
            }.sortedBy { it.label.lowercase() }

            _rawApps.value = appList

            // If user has no favorites configured yet, default to first 6 apps as favorites to show top pill
            if (_favorites.value.isEmpty() && appList.isNotEmpty()) {
                val defaultFavs = appList.take(6).map { it.packageName }
                saveFavorites(defaultFavs)
            }

            applyFilter()
        }
    }

    private fun applyFilter() {
        val hidden = _hiddenApps.value
        _apps.value = _rawApps.value.filter { !hidden.contains(it.packageName) }
    }

    fun toggleFavorite(packageName: String) {
        val current = _favorites.value.toMutableList()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            if (current.size < MAX_FAVORITES) current.add(packageName)
        }
        saveFavorites(current)
    }

    fun moveFavorite(packageName: String, direction: Int) {
        val current = _favorites.value.toMutableList()
        val index = current.indexOf(packageName)
        if (index == -1) return
        val newIndex = index + direction
        if (newIndex in 0 until current.size) {
            val item = current.removeAt(index)
            current.add(newIndex, item)
            saveFavorites(current)
        }
    }

    fun toggleHideApp(packageName: String) {
        val current = _hiddenApps.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
            if (_favorites.value.contains(packageName)) {
                val favs = _favorites.value.toMutableList()
                favs.remove(packageName)
                saveFavorites(favs)
            }
        }
        saveHidden(current)
    }

    fun unhideAllApps() {
        saveHidden(emptySet())
    }

    fun clearFavorites() {
        saveFavorites(emptyList())
    }

    fun isFavorite(packageName: String): Boolean = _favorites.value.contains(packageName)
    fun isHidden(packageName: String): Boolean = _hiddenApps.value.contains(packageName)

    private const val PREFS_NAME = "zen_launcher_prefs"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_FAVORITES_ORDERED = "favorites_ordered"
    private const val KEY_HIDDEN = "hidden_apps"
    const val MAX_FAVORITES = 8
}

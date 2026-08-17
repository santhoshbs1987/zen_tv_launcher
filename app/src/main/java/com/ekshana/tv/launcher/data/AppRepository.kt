package com.ekshana.tv.launcher.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
 * Singleton repository managing installed Android TV Leanback applications.
 *
 * Fast asynchronous decoding & downsampling pipeline:
 * - Prioritizes official 16:9 Leanback TV banners
 * - Falls back to standard icons
 * - Caches decoded ImageBitmaps in ConcurrentHashMap for instant rendering
 */
object AppRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val iconCache = ConcurrentHashMap<String, ImageBitmap>()

    private val _rawApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val rawApps: StateFlow<List<AppInfo>> = _rawApps.asStateFlow()

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _hiddenApps = MutableStateFlow<Set<String>>(emptySet())
    val hiddenApps: StateFlow<Set<String>> = _hiddenApps.asStateFlow()

    private val _customAppOrder = MutableStateFlow<List<String>>(emptyList())
    val customAppOrder: StateFlow<List<String>> = _customAppOrder.asStateFlow()

    private lateinit var prefs: SharedPreferences
    private lateinit var appContext: Context
    private var initialized = false
    val isInitialized: Boolean get() = initialized

    fun init(context: Context) {
        appContext = context.applicationContext
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _hiddenApps.value = prefs.getStringSet(KEY_HIDDEN, emptySet()) ?: emptySet()
        _customAppOrder.value = loadCustomOrderFromPrefs()
        initialized = true
        refresh()
    }

    private fun loadCustomOrderFromPrefs(): List<String> {
        val raw = prefs.getString(KEY_CUSTOM_ORDER, null) ?: return emptyList()
        return if (raw.isEmpty()) emptyList() else raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun saveCustomOrderToPrefs(order: List<String>) {
        val raw = order.joinToString(",")
        prefs.edit().putString(KEY_CUSTOM_ORDER, raw).apply()
    }

    /**
     * Queries PackageManager for Leanback apps, downsamples icons/banners, and updates StateFlow.
     */
    fun refresh() {
        scope.launch {
            val pm = appContext.packageManager

            val tvIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
            }
            @Suppress("DEPRECATION")
            val tvApps = pm.queryIntentActivities(tvIntent, 0)

            val combinedResolves = tvApps
                .filter { it.activityInfo.packageName != appContext.packageName }
                .distinctBy { it.activityInfo.packageName }

            val appList = combinedResolves.map { ri ->
                val pkg = ri.activityInfo.packageName
                val cached = iconCache[pkg]
                val bitmap = if (cached != null) {
                    cached
                } else {
                    try {
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
            }

            _rawApps.value = appList
            applyFilter()
        }
    }

    private fun applyFilter() {
        val hidden = _hiddenApps.value
        val unhiddenApps = _rawApps.value.filter { !hidden.contains(it.packageName) }
        val order = _customAppOrder.value

        if (order.isEmpty()) {
            _apps.value = unhiddenApps.sortedBy { it.label.lowercase() }
        } else {
            val orderMap = order.mapIndexed { index, pkg -> pkg to index }.toMap()
            // Apps with an established custom order rank first by their saved index;
            // Any new apps not in orderMap are sorted alphabetically after.
            _apps.value = unhiddenApps.sortedWith(
                compareBy<AppInfo> { app -> orderMap[app.packageName] ?: Int.MAX_VALUE }
                    .thenBy { it.label.lowercase() }
            )
        }
    }

    fun moveApp(fromIndex: Int, toIndex: Int) {
        val currentDisplay = _apps.value.toMutableList()
        if (fromIndex !in currentDisplay.indices || toIndex !in currentDisplay.indices || fromIndex == toIndex) {
            return
        }

        val item = currentDisplay.removeAt(fromIndex)
        currentDisplay.add(toIndex, item)

        val newOrder = currentDisplay.map { it.packageName }
        _customAppOrder.value = newOrder
        saveCustomOrderToPrefs(newOrder)
        applyFilter()
    }

    fun moveAppByPackage(packageName: String, delta: Int): Int {
        val currentDisplay = _apps.value
        val currentIndex = currentDisplay.indexOfFirst { it.packageName == packageName }
        if (currentIndex == -1) return -1

        val targetIndex = (currentIndex + delta).coerceIn(0, currentDisplay.size - 1)
        if (targetIndex != currentIndex) {
            moveApp(currentIndex, targetIndex)
        }
        return targetIndex
    }

    fun resetAppOrder() {
        _customAppOrder.value = emptyList()
        prefs.edit().remove(KEY_CUSTOM_ORDER).apply()
        applyFilter()
    }

    fun toggleHideApp(packageName: String) {
        val current = _hiddenApps.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _hiddenApps.value = current
        prefs.edit().putStringSet(KEY_HIDDEN, current).apply()
        applyFilter()
    }

    fun unhideAllApps() {
        _hiddenApps.value = emptySet()
        prefs.edit().putStringSet(KEY_HIDDEN, emptySet()).apply()
        applyFilter()
    }

    fun isHidden(packageName: String): Boolean = _hiddenApps.value.contains(packageName)

    private const val PREFS_NAME = "zen_launcher_prefs"
    private const val KEY_HIDDEN = "hidden_apps"
    private const val KEY_CUSTOM_ORDER = "custom_app_order"
}

package com.ekshana.tv.launcher.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Lightweight representation of an installed, launchable app.
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val iconBitmap: ImageBitmap? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppInfo) return false
        return packageName == other.packageName
    }

    override fun hashCode(): Int = packageName.hashCode()

    companion object {
        // Shared 1x1 transparent placeholder for instant zero-alloc frame rendering
        val placeholderBitmap: ImageBitmap by lazy {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap()
        }
    }
}

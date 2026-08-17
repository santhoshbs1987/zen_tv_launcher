package com.ekshana.tv.launcher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Android TV Native Input Manager Helper.
 *
 * Directly invokes the official Android TV Inputs Side Panel overlay:
 * - Primary: com.google.android.tvlauncher/.inputs.InputsPanelActivity
 * - Secondary: com.android.tv/.MainActivity
 * - Generic: android.media.tv.action.VIEW_INPUTS
 */
object TvInputManagerHelper {

    private val NATIVE_INPUT_COMPONENTS = listOf(
        ComponentName("com.google.android.tvlauncher", "com.google.android.tvlauncher.inputs.InputsPanelActivity"),
        ComponentName("com.android.tv", "com.android.tv.MainActivity"),
    )

    /**
     * Opens the native Android TV Inputs Side Menu overlay.
     */
    fun openNativeInputsMenu(context: Context): Boolean {
        for (component in NATIVE_INPUT_COMPONENTS) {
            try {
                val intent = Intent().apply {
                    setComponent(component)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return true
            } catch (_: Exception) {}
        }

        try {
            val intent = Intent("android.media.tv.action.VIEW_INPUTS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return true
        } catch (_: Exception) {}

        Toast.makeText(context, "TV Inputs menu not available", Toast.LENGTH_SHORT).show()
        return false
    }
}

package com.ekshana.tv.launcher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.net.Uri
import android.widget.Toast

data class TvInputItem(
    val id: String,
    val label: String,
    val description: String,
    val icon: String,
    val passthroughUri: Uri,
)

/**
 * Android TV Native Input Manager.
 *
 * Launches the native Android TV / Google TV Inputs Side Panel overlay:
 * - Primary: com.google.android.tvlauncher/.inputs.InputsPanelActivity (Official Android TV Inputs Side Menu)
 * - Secondary: Hardware port direct tuning (HDMI 1 ARC, HDMI 2, HDMI 3, AV, Antenna)
 */
object TvInputManagerHelper {

    private val NATIVE_INPUT_COMPONENTS = listOf(
        ComponentName("com.google.android.tvlauncher", "com.google.android.tvlauncher.inputs.InputsPanelActivity"),
        ComponentName("com.android.tv", "com.android.tv.MainActivity"),
    )

    /**
     * Opens the native Android TV Inputs Side Menu / Overlay.
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

        // Fallback: Try general TV Input action
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

    val inputs = listOf(
        TvInputItem(
            id = "com.droidlogic.tvinput/.services.Hdmi1InputService/HW5",
            label = "HDMI 1 (ARC)",
            description = "Audio Return Channel / Soundbar / Console",
            icon = "🔌",
            passthroughUri = TvContract.buildChannelUriForPassthroughInput("com.droidlogic.tvinput/.services.Hdmi1InputService/HW5")
        ),
        TvInputItem(
            id = "com.droidlogic.tvinput/.services.Hdmi2InputService/HW6",
            label = "HDMI 2",
            description = "Set-top box / Streaming stick / PC",
            icon = "🔌",
            passthroughUri = TvContract.buildChannelUriForPassthroughInput("com.droidlogic.tvinput/.services.Hdmi2InputService/HW6")
        ),
        TvInputItem(
            id = "com.droidlogic.tvinput/.services.Hdmi3InputService/HW7",
            label = "HDMI 3",
            description = "Gaming / Media player",
            icon = "🔌",
            passthroughUri = TvContract.buildChannelUriForPassthroughInput("com.droidlogic.tvinput/.services.Hdmi3InputService/HW7")
        ),
        TvInputItem(
            id = "com.droidlogic.tvinput/.services.AV1InputService/HW1",
            label = "AV (Composite)",
            description = "Yellow (Video) · White (L) · Red (R)",
            icon = "🟡",
            passthroughUri = TvContract.buildChannelUriForPassthroughInput("com.droidlogic.tvinput/.services.AV1InputService/HW1")
        ),
        TvInputItem(
            id = "com.droidlogic.tvinput/.services.ADTVInputService/HW16",
            label = "Antenna / Cable TV",
            description = "Coaxial RF connector / DTV",
            icon = "📡",
            passthroughUri = TvContract.buildChannelUriForPassthroughInput("com.droidlogic.tvinput/.services.ADTVInputService/HW16")
        ),
    )

    fun switchInput(context: Context, inputItem: TvInputItem) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, inputItem.passthroughUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    setClassName("com.android.tv", "com.android.tv.MainActivity")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(context, "Unable to switch to ${inputItem.label}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

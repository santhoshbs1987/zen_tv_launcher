package com.ekshana.tv.launcher.data

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
    val passthroughUri: Uri? = null,
)

/**
 * Exact hardware port mapping matching the physical TV panel:
 *  - HDMI 1 (ARC)
 *  - HDMI 2
 *  - HDMI 3
 *  - AV (Video / L / R RCA jacks)
 *  - Antenna / TV Tuner (RF coax)
 */
object TvInputManagerHelper {

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
            icon = "📼",
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

    /**
     * Opens the native Android TV Inputs Side Menu / Overlay.
     */
    fun openNativeInputsMenu(context: Context): Boolean {
        try {
            val intent = Intent().apply {
                setClassName("com.google.android.tvlauncher", "com.google.android.tvlauncher.inputs.InputsPanelActivity")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return true
        } catch (_: Exception) {}

        try {
            val intent = Intent("android.media.tv.action.VIEW_INPUTS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return true
        } catch (_: Exception) {}

        return false
    }

    fun switchInput(context: Context, inputItem: TvInputItem) {
        // Tuner-based Antenna/Live TV uses the Live TV activity directly (passthrough URIs are for HDMI/AV only)
        if (inputItem.id.contains("ADTV", ignoreCase = true) || inputItem.passthroughUri == null) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    setClassName("com.android.tv", "com.android.tv.TvActivity")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return
            } catch (_: Exception) {}

            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://android.media.tv/channel")).apply {
                    type = "vnd.android.cursor.dir/channel"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return
            } catch (_: Exception) {}

            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    setClassName("com.droidlogic.droidlivetv", "com.droidlogic.droidlivetv.shortcut.ShortCutActivity")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return
            } catch (_: Exception) {}
        }

        // Passthrough inputs (HDMI 1, HDMI 2, HDMI 3, AV)
        try {
            val intent = Intent(Intent.ACTION_VIEW, inputItem.passthroughUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    setClassName("com.android.tv", "com.android.tv.TvActivity")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(context, "Unable to switch to ${inputItem.label}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




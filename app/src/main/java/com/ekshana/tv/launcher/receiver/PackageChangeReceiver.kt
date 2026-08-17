package com.ekshana.tv.launcher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ekshana.tv.launcher.data.AppRepository

/**
 * Listens for package install / uninstall / update events and triggers an
 * app-list refresh.  Declared statically in the manifest so it works even when
 * the launcher Activity is not in the foreground.
 *
 * No foreground service or wake-lock is needed — BroadcastReceiver.onReceive()
 * has a 10-second budget which is far more than enough for a PackageManager query.
 */
class PackageChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_CHANGED,
            Intent.ACTION_PACKAGE_REPLACED,
            -> {
                if (!AppRepository.isInitialized) {
                    AppRepository.init(context)
                } else {
                    AppRepository.refresh()
                }
            }
        }
    }
}

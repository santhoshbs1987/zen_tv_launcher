package com.ekshana.tv.launcher

import android.app.Application
import com.ekshana.tv.launcher.data.AppRepository

class ZenLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppRepository.init(this)
    }
}

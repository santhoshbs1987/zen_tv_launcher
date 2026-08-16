package com.ekshana.tv.launcher.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppInfoTest {

    @Test
    fun `AppInfo equality is based on packageName`() {
        val app1 = AppInfo(
            label = "YouTube",
            packageName = "com.google.android.youtube.tv",
            iconBitmap = null
        )
        val app2 = AppInfo(
            label = "YouTube TV Edition",
            packageName = "com.google.android.youtube.tv",
            iconBitmap = null
        )
        val app3 = AppInfo(
            label = "Netflix",
            packageName = "com.netflix.ninja",
            iconBitmap = null
        )

        assertEquals(app1, app2)
        assertEquals(app1.hashCode(), app2.hashCode())
        assertNotEquals(app1, app3)
        assertFalse(app1.equals("NotAnAppInfo"))
    }

    @Test
    fun `AppInfo attributes are correctly set`() {
        val app = AppInfo(
            label = "Settings",
            packageName = "com.android.tv.settings"
        )

        assertEquals("Settings", app.label)
        assertEquals("com.android.tv.settings", app.packageName)
        assertEquals(null, app.iconBitmap)
    }
}

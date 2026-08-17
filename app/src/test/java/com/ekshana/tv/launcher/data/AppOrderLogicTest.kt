package com.ekshana.tv.launcher.data

import org.junit.Assert.assertEquals
import org.junit.Test

class AppOrderLogicTest {

    @Test
    fun `custom order places prioritized apps first and new apps alphabetically`() {
        val rawApps = listOf(
            AppInfo(label = "YouTube", packageName = "com.youtube"),
            AppInfo(label = "Netflix", packageName = "com.netflix"),
            AppInfo(label = "Amazon Prime", packageName = "com.amazon"),
            AppInfo(label = "Apple TV", packageName = "com.appletv"),
            AppInfo(label = "Disney+", packageName = "com.disney"),
        )

        // Custom order has Netflix first, Disney second
        val customOrder = listOf("com.netflix", "com.disney")
        val orderMap = customOrder.mapIndexed { index, pkg -> pkg to index }.toMap()

        val sorted = rawApps.sortedWith(
            compareBy<AppInfo> { app -> orderMap[app.packageName] ?: Int.MAX_VALUE }
                .thenBy { it.label.lowercase() }
        )

        assertEquals("Netflix", sorted[0].label)
        assertEquals("Disney+", sorted[1].label)
        // Rest should be sorted alphabetically: Amazon Prime, Apple TV, YouTube
        assertEquals("Amazon Prime", sorted[2].label)
        assertEquals("Apple TV", sorted[3].label)
        assertEquals("YouTube", sorted[4].label)
    }

    @Test
    fun `move app in list changes order correctly`() {
        val list = mutableListOf("appA", "appB", "appC", "appD")
        
        // Move appD (index 3) to index 0
        val item = list.removeAt(3)
        list.add(0, item)

        assertEquals(listOf("appD", "appA", "appB", "appC"), list)

        // Move appA (index 1) to index 2
        val item2 = list.removeAt(1)
        list.add(2, item2)

        assertEquals(listOf("appD", "appB", "appA", "appC"), list)
    }
}

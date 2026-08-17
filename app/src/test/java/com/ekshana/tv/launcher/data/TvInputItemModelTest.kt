package com.ekshana.tv.launcher.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TvInputItemModelTest {

    @Test
    fun `TvInputItem data class handles input metadata correctly`() {
        val item = TvInputItem(
            id = "com.droidlogic.tvinput/.services.Hdmi1InputService/HW5",
            label = "HDMI 1 (ARC)",
            description = "Audio Return Channel / Soundbar / Console",
            icon = "🔌",
            passthroughUri = null
        )

        assertEquals("com.droidlogic.tvinput/.services.Hdmi1InputService/HW5", item.id)
        assertEquals("HDMI 1 (ARC)", item.label)
        assertEquals("Audio Return Channel / Soundbar / Console", item.description)
        assertEquals("🔌", item.icon)
        assertNull(item.passthroughUri)
    }
}


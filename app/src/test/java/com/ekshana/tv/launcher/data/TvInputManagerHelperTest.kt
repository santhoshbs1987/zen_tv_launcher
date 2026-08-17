package com.ekshana.tv.launcher.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TvInputManagerHelperTest {

    @Test
    fun `default inputs list contains all 5 physical hardware ports`() {
        val inputs = TvInputManagerHelper.inputs
        assertEquals(5, inputs.size)

        val labels = inputs.map { it.label }
        assertTrue(labels.contains("HDMI 1 (ARC)"))
        assertTrue(labels.contains("HDMI 2"))
        assertTrue(labels.contains("HDMI 3"))
        assertTrue(labels.contains("AV (Composite)"))
        assertTrue(labels.contains("Antenna / Cable TV"))
    }

    @Test
    fun `inputs have valid passthrough URIs`() {
        for (input in TvInputManagerHelper.inputs) {
            assertNotNull(input.passthroughUri)
            assertTrue(input.passthroughUri.toString().startsWith("content://android.media.tv/passthrough/"))
            assertTrue(input.id.isNotBlank())
            assertTrue(input.icon.isNotBlank())
        }
    }
}

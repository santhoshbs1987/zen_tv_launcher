package com.ekshana.tv.launcher.data

import org.junit.Assert.assertEquals
import org.junit.Test

class TvRecommendationTest {

    @Test
    fun `TvRecommendation attributes are preserved`() {
        val rec = TvRecommendation(
            id = 42L,
            title = "Inception",
            description = "Sci-fi thriller",
            packageName = "com.primevideo.tv",
            posterArtUri = null,
            intentUri = null,
        )

        assertEquals(42L, rec.id)
        assertEquals("Inception", rec.title)
        assertEquals("Sci-fi thriller", rec.description)
        assertEquals("com.primevideo.tv", rec.packageName)
        assertEquals(null, rec.posterArtUri)
        assertEquals(null, rec.intentUri)
    }
}

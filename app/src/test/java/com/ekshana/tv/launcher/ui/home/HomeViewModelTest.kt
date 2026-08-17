package com.ekshana.tv.launcher.ui.home

import app.cash.turbine.test
import com.ekshana.tv.launcher.data.AppInfo
import com.ekshana.tv.launcher.data.TvRecommendation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HomeUiState holds correct initial default values`() {
        val uiState = HomeUiState()
        assertTrue(uiState.allApps.isEmpty())
        assertTrue(uiState.rawApps.isEmpty())
        assertTrue(uiState.recommendations.isEmpty())
        assertTrue(uiState.hiddenApps.isEmpty())
        assertTrue(uiState.isLoading)
    }

    @Test
    fun `HomeUiState correctly maps populated data`() {
        val app1 = AppInfo(label = "YouTube", packageName = "com.google.android.youtube.tv")
        val app2 = AppInfo(label = "Netflix", packageName = "com.netflix.ninja")
        val rec1 = TvRecommendation(
            id = 1L,
            title = "Stranger Things",
            description = "Season 4",
            packageName = "com.netflix.ninja",
            posterArtUri = null,
            intentUri = null,
        )

        val uiState = HomeUiState(
            allApps = listOf(app1, app2),
            rawApps = listOf(app1, app2),
            recommendations = listOf(rec1),
            hiddenApps = setOf("com.hidden.app"),
            isLoading = false
        )

        assertEquals(2, uiState.allApps.size)
        assertEquals(1, uiState.recommendations.size)
        assertEquals("Stranger Things", uiState.recommendations.first().title)
        assertTrue(uiState.hiddenApps.contains("com.hidden.app"))
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `StateFlow emissions work with Turbine`() = runTest {
        val stateFlow = MutableStateFlow<String?>("initial")

        stateFlow.asStateFlow().test {
            assertEquals("initial", awaitItem())

            stateFlow.value = "com.google.android.youtube.tv"
            assertEquals("com.google.android.youtube.tv", awaitItem())

            stateFlow.value = "com.netflix.ninja"
            assertEquals("com.netflix.ninja", awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}

package com.ekshana.tv.launcher

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.ekshana.tv.launcher.data.AppRepository
import com.ekshana.tv.launcher.ui.home.HomeScreen
import com.ekshana.tv.launcher.ui.theme.DarkBg
import com.ekshana.tv.launcher.ui.theme.ZenTvTheme

class MainActivity : ComponentActivity() {

    private var menuPressedTrigger by mutableStateOf(0L)

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZenTvTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBg),
                    shape = RectangleShape,
                ) {
                    HomeScreen(menuPressedTrigger = menuPressedTrigger)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (AppRepository.apps.value.isEmpty()) {
            AppRepository.refresh()
        }
    }

    /**
     * Intercept TV Remote Settings / Guide Keys
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SETTINGS || keyCode == KeyEvent.KEYCODE_GUIDE) {
            menuPressedTrigger = System.currentTimeMillis()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
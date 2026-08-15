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
    private var inputPressedTrigger by mutableStateOf(0L)

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
                    HomeScreen(
                        menuPressedTrigger = menuPressedTrigger,
                        inputPressedTrigger = inputPressedTrigger,
                    )
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
     * Intercept 11-Button Mi TV Remote Keys:
     * 1. Power (Top)
     * 2. D-pad Circle (Up, Down, Left, Right, Center/OK)
     * 3. Home Button (Pentagon / Outline icon) -> Returns directly to Zen TV home
     * 4. Back Button (< Arrow) -> Handled by Compose BackHandler
     * 5. Menu Button (☰ 3 horizontal bars) -> Opens Settings when on Home, or Context Menu on focused app
     * 6. Volume +/- Rocker
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            when (keyCode) {
                // Settings & Guide shortcuts
                KeyEvent.KEYCODE_SETTINGS,
                KeyEvent.KEYCODE_GUIDE -> {
                    menuPressedTrigger = System.currentTimeMillis()
                    return true
                }
                // Source / Input switching keys
                KeyEvent.KEYCODE_TV_INPUT,
                KeyEvent.KEYCODE_TV_INPUT_COMPOSITE_1,
                KeyEvent.KEYCODE_TV_INPUT_HDMI_1,
                KeyEvent.KEYCODE_TV_INPUT_HDMI_2,
                KeyEvent.KEYCODE_TV_INPUT_HDMI_3,
                KeyEvent.KEYCODE_TV_INPUT_HDMI_4,
                KeyEvent.KEYCODE_TV_RADIO_SERVICE,
                KeyEvent.KEYCODE_TV_TERRESTRIAL_ANALOG,
                KeyEvent.KEYCODE_TV_TERRESTRIAL_DIGITAL -> {
                    inputPressedTrigger = System.currentTimeMillis()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
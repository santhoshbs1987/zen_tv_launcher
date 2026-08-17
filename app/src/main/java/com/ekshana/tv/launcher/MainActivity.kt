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
import com.ekshana.tv.launcher.ui.theme.ZenBgTop
import com.ekshana.tv.launcher.ui.theme.ZenLauncherTheme

class MainActivity : ComponentActivity() {

    private var inputPressedTrigger by mutableStateOf(0L)

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZenLauncherTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ZenBgTop),
                    shape = RectangleShape,
                ) {
                    HomeScreen(
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
     * Intercept Remote D-pad & Source keys.
     *
     * NOTE on Xiaomi Mi TV 4A physical IR remote:
     * - Volume +/-  : Consumed by Android AudioManager at HAL level (never reaches app).
     * - Home (⊙)   : Consumed by Android ActivityManager (triggers home intent directly).
     * - Menu (☰)   : Consumed by Xiaomi firmware before Java — opens Xiaomi system panel.
     * Only the D-pad, OK, Back, and TV-Input source keycodes reach this activity.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.repeatCount == 0) {
            when (keyCode) {
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
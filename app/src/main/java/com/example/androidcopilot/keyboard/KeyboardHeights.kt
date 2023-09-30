package com.example.androidcopilot.keyboard

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.androidcopilot.app.ApplicationDependencies
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


object KeyboardHeights {

    private const val SP_KEYBOARD_HEIGHT = "keyboard-height"
    private const val KEY_LANDSCAPE_HEIGHT = "keyboard_height_landscape"
    private const val KEY_PORTRAIT_HEIGHT = "keyboard_height_portrait"

    private val landscapeHeight = MutableStateFlow(0)
    private val portraitHeight = MutableStateFlow(0)

    private fun Context.keyboardHeightPreferences(): SharedPreferences {
        return getSharedPreferences(SP_KEYBOARD_HEIGHT, Context.MODE_PRIVATE)
    }

    const val defaultMinHeight = 220

    fun getPortraitKeyboardHeight(context: Context = ApplicationDependencies.applicationContext): Int {
        if (portraitHeight.value > 0) {
            return portraitHeight.value
        }

        var height = context.keyboardHeightPreferences()
            .getInt(KEY_PORTRAIT_HEIGHT, 0)
        if (height == 0) {
            height = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("keyboard_height_portrait", 0)
        }
        if (height > 0) {
            portraitHeight.value = height
        }
        return height
    }

    fun setPortraitKeyboardHeight(height: Int, context: Context = ApplicationDependencies.applicationContext) {
        if (height < 0) {
            return
        }
        context.keyboardHeightPreferences()
            .edit()
            .putInt(KEY_PORTRAIT_HEIGHT, height)
            .apply()
        portraitHeight.value = height
    }


    fun getLandscapeKeyboardHeight(context: Context = ApplicationDependencies.applicationContext): Int {
        if (landscapeHeight.value > 0) {
            return landscapeHeight.value
        }

        var height = context.keyboardHeightPreferences()
            .getInt(KEY_LANDSCAPE_HEIGHT, 0)
        if (height == 0) {
            height = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("keyboard_height_landscape", 0)
        }

        if (height > 0) {
            landscapeHeight.value = height
        }
        return height
    }

    fun setLandscapeKeyboardHeight(height: Int, context: Context = ApplicationDependencies.applicationContext) {
        if (height < 0) {
            return
        }
        context.keyboardHeightPreferences()
            .edit()
            .putInt(KEY_LANDSCAPE_HEIGHT, height)
            .apply()
        landscapeHeight.value = height
    }

    enum class ImeState {
        ImeHiding,
        ImeHidden,
        ImeShowing,
        ImeShown
    }


    @Composable
    fun rememberKeyboardHeight(): Pair<ImeState, Dp> {
        val configuration = LocalConfiguration.current
        val landscape by remember {
            derivedStateOf {
                configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            }
        }
        val density = LocalDensity.current
        val imeInsets = WindowInsets.ime
        val systemHeight = WindowInsets.systemBars.getBottom(density)
        val imeHeight = imeInsets.getBottom(density)
        var lastImeHeight by remember {
            mutableStateOf(imeHeight)
        }
        var imeState by remember {
            mutableStateOf(ImeState.ImeHidden)
        }

        // ime insets will animated sdk 30+
        LaunchedEffect(imeHeight) {
            // wait ime animation finished
            if (imeHeight > lastImeHeight) {
                // showing
                val currentHeight = imeHeight
                launch {
                    delay(200)
                    awaitFrame()
                    if (currentHeight == imeInsets.getBottom(density)) {
                        // idle
                        imeState = ImeState.ImeShown
                    }
                }
                imeState = ImeState.ImeShowing
            } else if (imeHeight < lastImeHeight) {
                // hiding
                imeState = if (imeHeight <= 0) {
                    ImeState.ImeHidden
                } else {
                    ImeState.ImeHiding
                }
            } else if (imeHeight <= 0) {
                // hidden
                imeState = ImeState.ImeHidden
            } else {
                val currentHeight = imeHeight
                launch {
                    delay(200)
                    awaitFrame()
                    if (currentHeight == imeInsets.getBottom(density)) {
                        // idle
                        imeState = ImeState.ImeShown
                    }
                }
            }
            lastImeHeight = imeHeight
        }
        LaunchedEffect(imeState, imeHeight) {
            if (imeState == ImeState.ImeShown && imeHeight > systemHeight) {
                if (landscape) {
                    setLandscapeKeyboardHeight(imeHeight - systemHeight)
                } else {
                    setPortraitKeyboardHeight(imeHeight - systemHeight)
                }
            }
        }
        val keyboardHeight = with(LocalDensity.current) {
            var height = if (landscape) {
                getLandscapeKeyboardHeight(LocalContext.current)
            } else {
                getPortraitKeyboardHeight(LocalContext.current)
            }
            val minSize = defaultMinHeight.dp.roundToPx()
            height = if (height < minSize) {
                minSize
            } else {
                height
            }
            height.toDp()
        }
        return imeState to keyboardHeight
    }
}


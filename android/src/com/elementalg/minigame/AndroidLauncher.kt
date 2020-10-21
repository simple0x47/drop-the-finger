package com.elementalg.minigame

import com.badlogic.gdx.backends.android.AndroidApplication
import android.os.Bundle
import android.util.DisplayMetrics
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import java.util.*

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)

        val config = AndroidApplicationConfiguration()
        config.numSamples = 4
        initialize(Game(Locale.getDefault(), displayMetrics.xdpi, displayMetrics.ydpi), config)
    }
}
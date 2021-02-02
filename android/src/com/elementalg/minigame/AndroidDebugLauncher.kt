package com.elementalg.minigame

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import androidx.multidex.MultiDex
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import java.util.*

/**
 * Launcher used for debugging/development purposes only.
 *
 * @author Gabriel Amihalachioaie.
 */
class AndroidDebugLauncher : AndroidApplication() {
    private lateinit var game: Game
    private lateinit var adsBridge: MockAdsBridge
    private lateinit var leaderboard: MockLeaderboard

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)

        val config = AndroidApplicationConfiguration()
        config.numSamples = 2
        config.useImmersiveMode = true

        Toast.makeText(context, R.string.loading, Toast.LENGTH_SHORT).show()

        adsBridge = MockAdsBridge()
        adsBridge.load()

        val onScreenNotification: AndroidOnScreenNotification = AndroidOnScreenNotification(this)

        game = Game(
            Locale.getDefault(), displayMetrics.xdpi, displayMetrics.ydpi,
            adsBridge, onScreenNotification
        )

        leaderboard = MockLeaderboard()
        game.initializeLeaderboard(leaderboard)

        initialize(game, config)
    }

    companion object {

    }
}
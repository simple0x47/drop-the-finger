package com.elementalg.minigame.screens

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.elementalg.client.managers.DependencyManager
import com.elementalg.client.managers.Screen
import com.elementalg.minigame.Finger

import kotlin.math.min

class ContinuousModeScreen(private val displayXDPI: Float, private val displayYDPI: Float) : Screen() {
    private val backgroundViewport: FillViewport = FillViewport(20f, 20f)
    private val actorsViewport: StretchViewport = StretchViewport(20f, 20f)
    private val stage: Stage = Stage(actorsViewport)

    private lateinit var finger: Finger

    override fun create(dependencyManager: DependencyManager) {
        val fingerRadius: Float = (0.590551f * min(displayXDPI, displayYDPI)) / 2f

        finger = Finger(fingerRadius)
    }

    override fun show() {
        super.show()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    override fun render(delta: Float) {
        super.render(delta)
    }

    override fun pause() {
        super.pause()
    }

    override fun resume() {
        super.resume()
    }

    override fun hide() {
        super.hide()
    }

    override fun dispose() {
        super.dispose()
    }
}
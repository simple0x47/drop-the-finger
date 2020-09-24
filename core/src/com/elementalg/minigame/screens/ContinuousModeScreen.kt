package com.elementalg.minigame.screens

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.elementalg.client.managers.DependencyManager
import com.elementalg.client.managers.Screen
import com.elementalg.minigame.World

import kotlin.math.min

class ContinuousModeScreen(private val displayXDPI: Float, private val displayYDPI: Float) : Screen() {
    private val backgroundViewport: FillViewport = FillViewport(World.WORLD_SIZE, World.WORLD_SIZE)
    private val actorsViewport: StretchViewport = StretchViewport(World.WORLD_SIZE, World.WORLD_SIZE)
    private val stage: Stage = Stage(actorsViewport)

    private lateinit var world: World

    override fun create(dependencyManager: DependencyManager) {
        val fingerRadius: Float = FINGER_INCH_RADIUS * min(displayXDPI, displayYDPI)

        world = World()
        world.create(dependencyManager, fingerRadius)
    }

    override fun show() {
        super.show()
    }

    override fun resize(width: Int, height: Int) {
        backgroundViewport.update(width, height)
        actorsViewport.update(width, height)

        super.resize(width, height)
    }

    override fun render(delta: Float) {
        actorsViewport.apply()
        stage.batch.begin()
        world.draw(stage.batch)
        stage.batch.end()

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
        stage.dispose()
        world.dispose()

        super.dispose()
    }

    companion object {
        const val FINGER_INCH_RADIUS: Float = 0.1968504f
    }
}
package com.elementalg.minigame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.elementalg.client.managers.DependencyManager
import com.elementalg.client.managers.Screen
import com.elementalg.client.managers.ScreenManager
import com.elementalg.minigame.Game
import com.elementalg.minigame.world.Finger
import com.elementalg.minigame.world.BasicListener
import com.elementalg.minigame.world.SelfGeneratingWorld

import kotlin.math.min

/**
 * Infinite game mode based on the displacement and generation of a world.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an instance with the passed parameters.
 * @param displayXDPI density of pixels per inch on the x axis.
 * @param displayYDPI density of pixels per inch on the y axis.
 */
class ContinuousModeScreen(private val mainScreen: MainScreen, private val displayXDPI: Float,
                           private val displayYDPI: Float) : Screen() {
    private class GameOverListener(private val screen: ContinuousModeScreen) : BasicListener {
        override fun handle() {
            screen.showRestartWidget()
        }
    }

    private class RestartListener(private val screen: ContinuousModeScreen) : BasicListener {
        override fun handle() {
            screen.hideRestartWidget()
        }
    }

    private val backgroundViewport: FillViewport = FillViewport(SelfGeneratingWorld.WORLD_SIZE.x,
            SelfGeneratingWorld.WORLD_SIZE.y)
    private val actorsViewport: StretchViewport = StretchViewport(SelfGeneratingWorld.WORLD_SIZE.x,
            SelfGeneratingWorld.WORLD_SIZE.y)
    private val stage: Stage = Stage(actorsViewport)
    private val gameOverListener: GameOverListener = GameOverListener(this)

    private var drawRestartWidget: Boolean = false

    private lateinit var selfGeneratingWorld: SelfGeneratingWorld
    private lateinit var restartWidget: RestartWidget

    /**
     * Calculates the finger's radius in pixels, and initializes the world.
     */
    override fun create(game: Game) {
        val dependencyManager: DependencyManager = game.getDependencyManager()

        val fingerRadius: Float = Finger.FINGER_INCH_RADIUS * min(displayXDPI, displayYDPI)

        selfGeneratingWorld = SelfGeneratingWorld(stage, actorsViewport, gameOverListener)
        selfGeneratingWorld.create(dependencyManager, fingerRadius)

        restartWidget = RestartWidget(game, selfGeneratingWorld, mainScreen, RestartListener(this))
        restartWidget.create()
    }

    /**
     * Shows the world.
     */
    override fun show(screenManager: ScreenManager) {
        super.show()

        Gdx.input.inputProcessor = stage
        selfGeneratingWorld.show()
    }

    override fun resize(width: Int, height: Int) {
        backgroundViewport.update(width, height)
        actorsViewport.update(width, height)

        super.resize(width, height)
    }

    /**
     * Draws the world's actors.
     */
    override fun render(delta: Float) {
        actorsViewport.apply()
        stage.batch.projectionMatrix = actorsViewport.camera.combined
        stage.batch.begin()
        selfGeneratingWorld.render(stage.batch)
        stage.batch.end()

        if (!selfGeneratingWorld.isStarted()) {
            if (drawRestartWidget) {
                restartWidget.draw()
            }
        } else if (!drawRestartWidget) {
            drawRestartWidget = false
        }

        super.render(delta)
    }

    override fun pause() {
        super.pause()
    }

    override fun resume() {
        super.resume()
    }

    override fun hide() {
        selfGeneratingWorld.dispose()

        super.hide()
    }

    override fun dispose() {
        stage.dispose()
        selfGeneratingWorld.dispose()

        super.dispose()
    }

    fun showRestartWidget() {
        restartWidget.show()

        drawRestartWidget = true
    }

    fun hideRestartWidget() {
        restartWidget.hide()

        drawRestartWidget = false

        Gdx.input.inputProcessor = stage
    }
}
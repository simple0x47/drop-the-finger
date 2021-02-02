package com.elementalg.minigame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.elementalg.client.managers.DependencyManager
import com.elementalg.client.managers.Screen
import com.elementalg.client.managers.ScreenManager
import com.elementalg.minigame.Game
import com.elementalg.minigame.world.BasicListener
import com.elementalg.minigame.world.Finger
import com.elementalg.minigame.world.SelfGeneratingWorld
import com.elementalg.minigame.world.cells.CellContinuousGenerator
import kotlin.math.pow

/**
 * Infinite game mode based on the displacement and generation of a world.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an instance with the passed parameters.
 * @param displayXDPI density of pixels per inch on the x axis.
 * @param displayYDPI density of pixels per inch on the y axis.
 */
class ContinuousModeScreen(
    private val mainScreen: MainScreen, private val displayXDPI: Float,
    private val displayYDPI: Float
) : Screen() {
    private var retriesWithoutAds: Int = 0
    private var lastAdSince: Float = 0f

    private class GameOverListener(private val screen: ContinuousModeScreen) : BasicListener {
        override fun handle() {
            screen.showRestartWindow()
        }
    }

    private class RestartListener(private val screen: ContinuousModeScreen) : BasicListener {
        override fun handle() {
            screen.selfGeneratingWorld.regenerateWorld()

            if (screen.goingToShowAd()) {
                screen.selfGeneratingWorld.stopGameOverSound()
                val adListener: RestartAdsListener = RestartAdsListener(
                    screen,
                    screen.selfGeneratingWorld,
                    screen.stage
                )
                adListener.runBeforeAd()

                Game.instance().getAdsBridge().show(adListener)
            } else {
                screen.hideRestartWindow()
                screen.selfGeneratingWorld.restart(false)
                Gdx.input.inputProcessor = screen.stage
                screen.retriesWithoutAds += 1
            }
        }
    }

    private val backgroundViewport: FillViewport = FillViewport(
        SelfGeneratingWorld.WORLD_SIZE.x,
        SelfGeneratingWorld.WORLD_SIZE.y
    )
    private val actorsViewport: StretchViewport = StretchViewport(
        SelfGeneratingWorld.WORLD_SIZE.x,
        SelfGeneratingWorld.WORLD_SIZE.y
    )
    private val userInterfaceViewport: ScreenViewport = ScreenViewport()
    private val stage: Stage = Stage(actorsViewport)
    private val uiStage: Stage = Stage(userInterfaceViewport)
    private val gameOverListener: GameOverListener = GameOverListener(this)

    private var drawRestartWidget: Boolean = false

    private lateinit var selfGeneratingWorld: SelfGeneratingWorld
    private lateinit var restartWindow: RestartWindow
    private lateinit var scoreWidget: ScoreWidget

    /**
     * Calculates the finger's radius in pixels, and initializes the world.
     */
    override fun create(game: Game) {
        val dependencyManager: DependencyManager = game.getDependencyManager()

        val fingerRadius: Float = calculateFingerRadius()

        selfGeneratingWorld = SelfGeneratingWorld(stage, actorsViewport, gameOverListener)
        selfGeneratingWorld.create(dependencyManager, fingerRadius)

        restartWindow = RestartWindow(game, mainScreen, RestartListener(this))
        restartWindow.create()

        val assets: HashMap<String, Any> = dependencyManager.retrieveAssets("CONTINUOUS_MODE_SCREEN")

        check(assets.containsKey("Text")) { "ContinuousModeScreen dependency 'Text' is not solved." }
        check(assets.containsKey("Value")) { "ContinuousModeScreen dependency 'Value' is not solved." }

        val textFont: BitmapFont = assets["Text"] as BitmapFont
        val valueFont: BitmapFont = assets["Value"] as BitmapFont

        userInterfaceViewport.unitsPerPixel = 0.01f
        userInterfaceViewport.screenY = 0

        scoreWidget = ScoreWidget(textFont, valueFont, Color(0x29EFFFFF))
    }

    /**
     * Shows the world.
     */
    override fun show(screenManager: ScreenManager) {
        super.show()

        Gdx.input.inputProcessor = stage
        selfGeneratingWorld.regenerateWorld()
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

        if (selfGeneratingWorld.isStarted()) {
            lastAdSince += Gdx.graphics.deltaTime

            userInterfaceViewport.apply()
            uiStage.batch.projectionMatrix = userInterfaceViewport.camera.combined
            uiStage.batch.begin()
            scoreWidget.draw(uiStage.batch)
            uiStage.batch.end()
        }

        if (!selfGeneratingWorld.isStarted()) {
            if (drawRestartWidget) {
                restartWindow.draw()
            }
        } else if (!drawRestartWidget) {
            drawRestartWidget = false
        }

        scoreWidget.updateScore(selfGeneratingWorld.getScore())

        super.render(delta)
    }

    override fun pause() {
        super.pause()

        selfGeneratingWorld.pause()

        if (drawRestartWidget) {
            restartWindow.pause()
        }
    }

    override fun resume() {
        super.resume()

        if (drawRestartWidget) {
            restartWindow.resume()
        }
    }

    override fun hide() {
        selfGeneratingWorld.hide()

        super.hide()
    }

    override fun dispose() {
        stage.dispose()
        uiStage.dispose()
        selfGeneratingWorld.dispose()

        super.dispose()
    }

    fun showRestartWindow() {
        restartWindow.show(selfGeneratingWorld.getScore())

        drawRestartWidget = true
    }

    fun hideRestartWindow() {
        restartWindow.hide()

        drawRestartWidget = false
    }

    fun goingToShowAd(): Boolean {
        return ((retriesWithoutAds >= MAX_RETRIES_BEFORE_AD) ||
                (lastAdSince >= MAX_TIME_BEFORE_AD))
    }

    fun restartAdsStats() {
        retriesWithoutAds = 0
        lastAdSince = 0f
    }

    private fun calculateFingerRadius(): Float {
        val minimumCellHolderInnerSize: Float = SelfGeneratingWorld.WORLD_SIZE.x /
                (2f.pow(CellContinuousGenerator.CELL_HOLDER_UNTIL_LEVEL + 1))

        val originalRadius = Finger.FINGER_INCH_RADIUS * displayXDPI *
                SelfGeneratingWorld.WORLD_SIZE.x / Gdx.graphics.width

        return if ((originalRadius * 2f * Finger.FINGER_RADIUS_MARGIN) < minimumCellHolderInnerSize) {
            /*
             * Since multiple nesting levels, decrease the overall fluidity of the game, we will increase the finger
             * radius to the minimum cell holder inner size by the finger required margin.
            */
            (minimumCellHolderInnerSize * Finger.FINGER_RADIUS_MARGIN) / 2f
        } else {
            originalRadius
        }
    }

    companion object {
        private const val MAX_RETRIES_BEFORE_AD: Int = 5
        private const val MAX_TIME_BEFORE_AD: Float = 100f // 100 seconds
    }
}
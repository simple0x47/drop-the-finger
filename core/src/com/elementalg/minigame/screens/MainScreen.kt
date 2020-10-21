package com.elementalg.minigame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.elementalg.client.managers.DependencyManager
import com.elementalg.client.managers.Screen
import com.elementalg.client.managers.ScreenManager
import com.elementalg.minigame.Game
import kotlin.jvm.Throws

/**
 * Screen containing the main menu of the game.
 *
 * @author Gabriel Amihalachioaie & Alberto Moreno Bonillo.
 *
 * @constructor initializes an instance with the passed parameters.
 * @param displayXDPI density of pixels per inch on the x axis.
 * @param displayYDPI density of pixels per inch on the y axis.
 */
class MainScreen(private val displayXDPI: Float, private val displayYDPI: Float) : Screen() {
    /**
     * Click listener for the play button.
     *
     * @param screenManager instance of [ScreenManager] used for this game's instance in order to change the screen to
     * the passed instance of [ContinuousModeScreen].
     * @param modeScreen instance of [ContinuousModeScreen] to be shown when the button is clicked.
     */
    private class PlayButtonListener(private val screenManager: ScreenManager,
                                     private val modeScreen: ContinuousModeScreen)
        : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            screenManager.setActiveScreen(modeScreen)

            super.clicked(event, x, y)
        }
    }

    /**
     * Click listener for the high score button.
     */
    private class HighScoreButtonListener : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            super.clicked(event, x, y)
        }
    }

    private val stageViewport: FillViewport = FillViewport(1f, 1f)
    private val actorsViewport: FitViewport = FitViewport(1f, 1f)
    private val stage: Stage = Stage(actorsViewport)

    private lateinit var background: TextureRegion
    private lateinit var playButton: Button
    private lateinit var highScoreButton: Button
    private lateinit var logo: Button
    
    private lateinit var modeScreen: ContinuousModeScreen

    /**
     * Initializes the main menu's design and UI elements.
     *
     * @param game instance of the game.
     *
     * @throws IllegalStateException if 'MAIN_SCREEN' dependency is not loaded yet or if an asset could not be solved.
     */
    @Throws(IllegalStateException::class)
    override fun create(game: Game) {
        val dependencyManager: DependencyManager = game.getDependencyManager()

        check(dependencyManager.isDependencyAvailable("MAIN_SCREEN")) {
            "'MAIN_SCREEN' is not loaded yet."
        }

        val assets: HashMap<String, Any> = dependencyManager.retrieveAssets("MAIN_SCREEN")

        check(assets.containsKey("MainScreenAtlas")) {
            "Main screen dependency 'MainScreenAtlas' is not solved."
        }

        val atlas: TextureAtlas = assets["MainScreenAtlas"] as TextureAtlas

        background = atlas.findRegion("Background")

        playButton = Button(TextureRegionDrawable(atlas.findRegion("PlayButton")))
        playButton.setSize(0.333823529f, 0.28921569f)
        playButton.setPosition(0.33333333f, 0.569117647f, Align.center)

        highScoreButton = Button(TextureRegionDrawable(atlas.findRegion("HighScoreButton")))
        highScoreButton.setSize(0.333823529f, 0.28921569f)
        highScoreButton.setPosition(0.667156862f, 0.424509803f, Align.center)

        logo = Button(TextureRegionDrawable(atlas.findRegion("LogoButton")))
        logo.setSize(0.10490196078f, 0.10490196078f)
        logo.setPosition(0.667156762f, 0.76617647f, Align.center)

        modeScreen = ContinuousModeScreen(this, displayXDPI, displayYDPI)
        modeScreen.create(game)
    }

    /**
     * Shows the main menu's actors.
     *
     * @param screenManager instance of [ScreenManager] used for this game's instance.
     */
    override fun show(screenManager: ScreenManager) {
        stage.addActor(playButton)
        stage.addActor(highScoreButton)
        stage.addActor(logo)

        playButton.addListener(PlayButtonListener(screenManager, modeScreen))
        highScoreButton.addListener(HighScoreButtonListener())

        Gdx.input.inputProcessor = stage

        super.show()
    }

    override fun resize(width: Int, height: Int) {
        stageViewport.update(width, height, true)
        actorsViewport.update(width, height, true)

        super.resize(width, height)
    }

    /**
     * Draws the main menu's actors and background.
     */
    override fun render(delta: Float) {
        stageViewport.apply(true)
        stage.batch.begin()
        stage.batch.draw(background, 0f, 0f, 1f, 1f)
        stage.batch.end()

        actorsViewport.apply(true)
        stage.act()
        stage.draw()

        super.render(delta)
    }

    override fun pause() {
        super.pause()
    }

    override fun resume() {
        super.resume()
    }

    /**
     * Hides the main menu.
     */
    override fun hide() {
        playButton.clearListeners()
        playButton.remove()

        highScoreButton.clearListeners()
        highScoreButton.remove()

        Gdx.input.inputProcessor = null

        super.hide()
    }

    override fun dispose() {
        stage.dispose()

        super.dispose()
    }
}
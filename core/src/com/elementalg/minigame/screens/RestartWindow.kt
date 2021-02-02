package com.elementalg.minigame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.elementalg.client.managers.LocaleManager
import com.elementalg.client.managers.ScreenManager
import com.elementalg.minigame.Game
import com.elementalg.minigame.world.BasicListener

class RestartWindow(private val game: Game, mainScreen: MainScreen, restartListener: BasicListener) {
    private class RestartButtonListener(
        private val restartWindow: RestartWindow,
        private val restartListener: BasicListener
    ) : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            restartWindow.hide()

            restartListener.handle()

            super.clicked(event, x, y)
        }
    }

    private class BackButtonListener(
        private val restartWindow: RestartWindow, private val screenManager: ScreenManager,
        private val mainScreen: MainScreen
    ) :
        ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            restartWindow.hide()
            screenManager.setActiveScreen(mainScreen)

            super.clicked(event, x, y)
        }
    }

    private val actorsViewport: FitViewport = FitViewport(1024f, 1024f)
    private val stage: Stage = Stage(actorsViewport)

    private val restartButtonListener: RestartButtonListener = RestartButtonListener(
        this,
        restartListener
    )
    private val backButtonListener: BackButtonListener = BackButtonListener(
        this, game.getScreenManager(),
        mainScreen
    )

    private lateinit var restartButton: Button
    private lateinit var backButton: Button

    private lateinit var scoreTitle: Label
    private lateinit var scoreValue: Label
    private lateinit var scoreMessage: Label
    private lateinit var scoreMessageProvider: ScoreMessageProvider
    private lateinit var scoreFireworks: ScoreFireworks

    private lateinit var highScoreMusic: Music

    private var wasHighScoreMusicPlaying: Boolean = false

    fun create() {
        val dependencyManager = game.getDependencyManager()

        check(dependencyManager.isDependencyAvailable(DEPENDENCY_NAME)) { "'$DEPENDENCY_NAME' is not loaded yet." }

        val assets: HashMap<String, Any> = dependencyManager.retrieveAssets(DEPENDENCY_NAME)

        check(assets.containsKey(WIDGET_ATLAS)) {
            "Main screen dependency '$WIDGET_ATLAS' is not solved."
        }

        val atlas: TextureAtlas = assets[WIDGET_ATLAS] as TextureAtlas

        restartButton = Button(TextureRegionDrawable(atlas.findRegion("RestartButton")))
        restartButton.setSize(371f, 321f)
        restartButton.setPosition(327f, 351f, Align.bottomLeft)

        backButton = Button(TextureRegionDrawable(atlas.findRegion("BackButton")))
        backButton.setSize(125f, 109f)
        backButton.setPosition(698f, 296f, Align.bottomLeft)

        val scoreTextFont: BitmapFont = assets[SCORE_TEXT_FONT] as BitmapFont
        val scoreValueFont: BitmapFont = assets[SCORE_VALUE_FONT] as BitmapFont

        val scoreTextStyle: Label.LabelStyle = Label.LabelStyle(scoreTextFont, Color(0x29EFFFFF))
        val scoreValueStyle: Label.LabelStyle = Label.LabelStyle(scoreValueFont, Color(0.6f, 0.97f, 1f, 1f))

        scoreTitle = Label("", scoreTextStyle)
        scoreTitle.setAlignment(Align.center, Align.center)
        scoreTitle.setSize(824f, 84f)
        scoreTitle.setPosition(100f, 906f, Align.bottomLeft)
        scoreValue = Label("", scoreValueStyle)
        scoreValue.setAlignment(Align.center, Align.center)
        scoreValue.setSize(824f, 84f)
        scoreValue.setPosition(100f, 766f, Align.bottomLeft)
        scoreMessage = Label("", scoreTextStyle)
        scoreMessage.setAlignment(Align.center, Align.center)
        scoreMessage.setSize(824f, 168f)
        scoreMessage.setPosition(100f, 74f, Align.bottomLeft)
        scoreMessage.wrap = true

        scoreMessageProvider = ScoreMessageProvider()

        val fireworksPosition: Vector2 = Vector2(100f, 766f)
        val fireworksSize: Vector2 = Vector2(824f, 212f)
        scoreFireworks = ScoreFireworks(
            fireworksPosition, fireworksSize,
            MAX_FIREWORKS, atlas
        )

        highScoreMusic = assets[HIGH_SCORE_MUSIC] as Music
    }

    fun show(score: Float) {
        stage.addActor(restartButton)
        restartButton.addListener(restartButtonListener)

        stage.addActor(backButton)
        backButton.addListener(backButtonListener)

        val previousHighScore: Float = Game.instance().getLeaderboard().getHighScore()
        val worldHighScore: Float = Game.instance().getLeaderboard().getWorldHighScore()
        val localeManager: LocaleManager = Game.instance().getLocaleManager()

        if (previousHighScore < score) {
            scoreTitle.setText(localeManager.get("RESTART_HIGH_SCORE_TITLE"))
        } else {
            scoreTitle.setText(localeManager.get("RESTART_SCORE_TITLE"))
        }

        scoreValue.setText(String.format("%.1fs", score))

        val messageType: ScoreMessage.Type = when {
            score > worldHighScore -> ScoreMessage.Type.GOAT
            score > previousHighScore -> ScoreMessage.Type.GOOD
            (score <= previousHighScore) && (score > PATHETIC_SCORE) -> ScoreMessage.Type.NEUTRAL
            else -> ScoreMessage.Type.BAD
        }

        scoreMessage.setText(scoreMessageProvider.retrieveMessage(messageType))
        stage.addActor(scoreTitle)
        stage.addActor(scoreValue)
        stage.addActor(scoreMessage)

        if (score > previousHighScore) {
            scoreFireworks.show()

            highScoreMusic.position = 0f
            highScoreMusic.volume = HIGH_SCORE_VOLUME
            highScoreMusic.isLooping = false
            highScoreMusic.play()
        } else {
            scoreFireworks.hide()
        }

        Gdx.input.inputProcessor = stage
    }

    fun draw() {
        actorsViewport.apply(true)
        stage.draw()

        stage.batch.begin()
        scoreFireworks.draw(stage.batch, Gdx.graphics.deltaTime)
        stage.batch.end()
    }

    fun pause() {
        if (highScoreMusic.isPlaying) {
            highScoreMusic.pause()
            wasHighScoreMusicPlaying = true
        }
    }

    fun resume() {
        if (wasHighScoreMusicPlaying) {
            highScoreMusic.play()
        }
    }

    fun hide() {
        restartButton.clearListeners()
        restartButton.remove()
        backButton.clearListeners()
        backButton.remove()

        scoreTitle.remove()
        scoreValue.remove()
        scoreMessage.remove()

        scoreFireworks.hide()

        highScoreMusic.stop()
        wasHighScoreMusicPlaying = false

        Gdx.input.inputProcessor = null
    }

    companion object {
        private const val DEPENDENCY_NAME: String = "RESTART_WIDGET"
        private const val WIDGET_ATLAS: String = "RestartWidgetAtlas"
        private const val SCORE_TEXT_FONT: String = "ScoreText"
        private const val SCORE_VALUE_FONT: String = "ScoreValue"
        private const val HIGH_SCORE_MUSIC: String = "HighScore"

        private const val PATHETIC_SCORE: Float = 10f

        private const val MAX_FIREWORKS: Int = 30

        private const val HIGH_SCORE_VOLUME: Float = 0.4f
    }
}
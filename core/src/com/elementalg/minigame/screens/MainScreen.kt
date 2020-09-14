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

class MainScreen(private val displayXDPI: Float, private val displayYDPI: Float) : Screen(){
    private class PlayButtonListener : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {


            super.clicked(event, x, y)
        }
    }

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
    private lateinit var logo: TextureRegion

    private lateinit var theme: Music

    override fun create(dependencyManager: DependencyManager) {
        check(dependencyManager.isDependencyAvailable("MAIN_SCREEN")){"'MAIN_SCREEN' is not loaded yet."}

        val assets: HashMap<String, Any> = dependencyManager.retrieveAssets("MAIN_SCREEN")

        check(assets.containsKey("MainScreenAtlas")) {
            "Main screen dependency 'MainScreenAtlas' is not solved."
        }

        val atlas: TextureAtlas = assets["MainScreenAtlas"] as TextureAtlas

        background = atlas.findRegion("Background")

        playButton = Button(TextureRegionDrawable(atlas.findRegion("PlayButton")))
        playButton.setSize(0.333823529f, 0.28921569f)
        playButton.setPosition(0.5f, 0.644607845f, Align.center)

        highScoreButton = Button(TextureRegionDrawable(atlas.findRegion("HighScoreButton")))
        highScoreButton.setSize(0.333823529f, 0.28921569f)
        highScoreButton.setPosition(0.5f, 0.355392155f, Align.center)

        logo = atlas.findRegion("Logo")

        theme = assets["MainScreenTheme"] as Music
    }

    override fun show() {
        stage.addActor(playButton)
        stage.addActor(highScoreButton)

        playButton.addListener(PlayButtonListener())
        highScoreButton.addListener(HighScoreButtonListener())

        Gdx.input.inputProcessor = stage

        theme.isLooping = true
        theme.play()
        theme.volume = 0.2f

        super.show()
    }

    override fun resize(width: Int, height: Int) {
        stageViewport.update(width, height, true)
        actorsViewport.update(width, height, true)

        super.resize(width, height)
    }

    override fun render(delta: Float) {
        stageViewport.apply(true)
        stage.batch.begin()
        stage.batch.draw(background, 0f, 0f, 1f, 1f)
        stage.batch.draw(logo, 0.45f, 0.05f, 0.1f, 0.091556f)
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

    override fun hide() {
        playButton.clearListeners()
        playButton.remove()

        highScoreButton.clearListeners()
        highScoreButton.remove()

        Gdx.input.inputProcessor = null

        theme.stop()

        super.hide()
    }

    override fun dispose() {
        stage.dispose()
        theme.dispose()

        super.dispose()
    }
}
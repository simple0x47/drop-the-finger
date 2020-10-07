package com.elementalg.minigame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.elementalg.client.managers.ScreenManager
import com.elementalg.minigame.Game
import com.elementalg.minigame.world.BasicListener
import com.elementalg.minigame.world.SelfGeneratingWorld

class RestartWidget(private val game: Game, selfGeneratingWorld: SelfGeneratingWorld, mainScreen: MainScreen,
                    restartListener: BasicListener) {
    private class RestartButtonListener(private val restartWidget: RestartWidget,
                                        private val world: SelfGeneratingWorld,
                                        private val restartListener: BasicListener) : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            restartWidget.hide()
            world.restart()
            restartListener.handle()

            super.clicked(event, x, y)
        }
    }

    private class BackButtonListener(private val restartWidget: RestartWidget,
                                     private val screenManager: ScreenManager, private val mainScreen: MainScreen) :
            ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            restartWidget.hide()
            screenManager.setActiveScreen(mainScreen)

            super.clicked(event, x, y)
        }
    }

    private val actorsViewport: FitViewport = FitViewport(1f, 1f)
    private val stage: Stage = Stage(actorsViewport)

    private val restartButtonListener: RestartButtonListener = RestartButtonListener(this, selfGeneratingWorld,
            restartListener)
    private val backButtonListener: BackButtonListener = BackButtonListener(this, game.getScreenManager(), mainScreen)

    private lateinit var restartButton: Button
    private lateinit var backButton: Button

    fun create() {
        val dependencyManager = game.getDependencyManager()

        check(dependencyManager.isDependencyAvailable(DEPENDENCY_NAME)){"'$DEPENDENCY_NAME' is not loaded yet."}

        val assets: HashMap<String, Any> = dependencyManager.retrieveAssets(DEPENDENCY_NAME)

        check(assets.containsKey(WIDGET_ATLAS)) {
            "Main screen dependency '$WIDGET_ATLAS' is not solved."
        }

        val atlas: TextureAtlas = assets[WIDGET_ATLAS] as TextureAtlas

        restartButton = Button(TextureRegionDrawable(atlas.findRegion("RestartButton")))
        restartButton.setSize(0.333823529f, 0.28921569f)
        restartButton.setPosition(0.33333333f, 0.569117647f, Align.center)

        backButton = Button(TextureRegionDrawable(atlas.findRegion("BackButton")))
        backButton.setSize(0.333823529f, 0.28921569f)
        backButton.setPosition(0.667156862f, 0.424509803f, Align.center)
    }

    fun show() {
        stage.addActor(restartButton)
        restartButton.addListener(restartButtonListener)

        stage.addActor(backButton)
        backButton.addListener(backButtonListener)

        Gdx.input.inputProcessor = stage
    }

    fun draw() {
        actorsViewport.apply(true)
        stage.draw()
    }

    fun hide() {
        restartButton.clearListeners()
        restartButton.remove()
        backButton.clearListeners()
        backButton.remove()

        Gdx.input.inputProcessor = null
    }

    companion object {
        private const val DEPENDENCY_NAME: String = "RESTART_WIDGET"
        private const val WIDGET_ATLAS: String = "RestartWidgetAtlas"
    }
}
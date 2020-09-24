package com.elementalg.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.elementalg.client.ui.Notification

abstract class Screen : com.badlogic.gdx.Screen {
    private val viewport: FitViewport = FitViewport(UI_WORLD_WIDTH, UI_WORLD_HEIGHT)
    private val stage: Stage = Stage(viewport)

    open fun show(screenManager: ScreenManager) {

    }

    override fun show() {

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        stage.draw()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {

    }

    override fun dispose() {
        stage.dispose()
    }

    abstract fun create(dependencyManager: DependencyManager)

    fun announce(notification: Notification) {
        stage.addActor(notification)
        notification.show(Gdx.input.inputProcessor)
        Gdx.input.inputProcessor = stage
    }

    companion object {
        const val UI_WORLD_WIDTH = 9f
        const val UI_WORLD_HEIGHT = 16f
    }
}
package com.elementalg.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.elementalg.managers.IUpdatableManager

/**
 * Manages the active screen, by connecting it to the <i>essentials</i> calls.
 *
 * @author Gabriel Amihalachioaie.
 */
class ScreenManager private constructor() : IUpdatableManager {

    private var activeScreen: Screen? = null

    /**
     * Hides the previously active screen, and proceeds to show the passed [screen].
     *
     * @param screen instance of [Screen] to be shown.
     */
    fun setActiveScreen(screen: Screen) {
        activeScreen?.hide()

        screen.show(this)
        this.activeScreen = screen
    }

    override fun create() {

    }

    /**
     * Clears the screen and renders the [activeScreen].
     */
    override fun update() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        activeScreen?.render(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        activeScreen?.dispose()
    }

    fun resize(width: Int, height: Int) {
        activeScreen?.resize(width, height)
    }

    fun pause() {
        activeScreen?.pause()
    }

    fun resume() {
        activeScreen?.resume()
    }

    companion object Factory {
        /**
         * Returns an instance of [ScreenManager].
         *
         * @throws IllegalStateException if [Gdx] has not been initialized yet.
         *
         * @return instance of [ScreenManager].
         */
        fun build(): ScreenManager {
            checkNotNull(Gdx.graphics) { "'Gdx' has not been initalized yet, and 'ScreenManager' depends on it." }

            return ScreenManager()
        }
    }
}
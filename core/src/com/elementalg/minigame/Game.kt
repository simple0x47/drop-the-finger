package com.elementalg.minigame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.elementalg.client.managers.DependencyManager
import com.elementalg.client.managers.LocaleManager
import com.elementalg.client.managers.ScreenManager
import com.elementalg.managers.EventManager
import com.elementalg.minigame.screens.MainScreen
import java.util.*
import kotlin.jvm.Throws
import kotlin.math.min

class Game(private val systemLocale: Locale, private val displayXDPI: Float, private val displayYDPI: Float)
    : ApplicationAdapter() {

    private val eventManager: EventManager = EventManager()

    private lateinit var dependencyManager: DependencyManager
    private lateinit var localeManager: LocaleManager
    private lateinit var screenManager: ScreenManager

    /**
     * Returns the instance of [EventManager] used for the active [Game] instance.
     *
     * @return instance of [EventManager].
     */
    fun getEventManager(): EventManager {

        return eventManager
    }

    /**
     * Returns the instance of [DependencyManager] used for the active [Game] instance.
     *
     * @throws IllegalStateException if [DependencyManager] has not been initialized yet.
     *
     * @return instance of [DependencyManager].
     */
    fun getDependencyManager(): DependencyManager {
        check(this::dependencyManager.isInitialized){"'dependencyManager' has not been initialized yet."}

        return dependencyManager
    }

    /**
     * Returns the instance of [LocaleManager] used for the active [Game] instance.
     *
     * @throws IllegalStateException if [LocaleManager] has not been initialized yet.
     *
     * @return instance of [LocaleManager].
     */
    fun getLocaleManager(): LocaleManager {
        check(this::localeManager.isInitialized){"'localeManager' has not been initialized yet."}

        return localeManager
    }

    /**
     * Returns the instance of [ScreenManager] used for the active [Game] instance.
     *
     * @throws IllegalStateException if [ScreenManager] has not been initialized yet.
     *
     * @return instance of [ScreenManager].
     */
    fun getScreenManager(): ScreenManager {
        check(this::screenManager.isInitialized){"'screenManager' has not been initialized yet."}

        return screenManager
    }

    override fun create() {
        gameInstance = this

        eventManager.create()

        dependencyManager = DependencyManager.build()
        dependencyManager.create()
        localeManager = LocaleManager.build(systemLocale)
        localeManager.create()
        screenManager = ScreenManager.build()
        screenManager.create()

        dependencyManager.loadDependencyID("MAIN_SCREEN")

        val mainScreen: MainScreen = MainScreen(displayXDPI, displayYDPI)
        mainScreen.create(dependencyManager)
        screenManager.setActiveScreen(mainScreen)
    }

    override fun render() {
        eventManager.update()

        dependencyManager.update()
        screenManager.update()
    }

    override fun resize(width: Int, height: Int) {
        screenManager.resize(width, height)
    }

    override fun pause() {
        screenManager.pause()
    }

    override fun resume() {
        screenManager.resume()
    }

    override fun dispose() {
        eventManager.dispose()

        dependencyManager.dispose()
        localeManager.dispose()
        screenManager.dispose()
    }

    companion object {
        private lateinit var gameInstance: Game

        @Throws(IllegalStateException::class)
        fun instance(): Game {
            check(this::gameInstance.isInitialized){"'gameInstance' has not been initialized yet."}

            return gameInstance
        }
    }
}
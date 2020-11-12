package com.elementalg.minigame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.elementalg.minigame.IAdsListener
import com.elementalg.minigame.world.SelfGeneratingWorld

class RestartAdsListener(private val screen: ContinuousModeScreen,
                         private val world: SelfGeneratingWorld,
                         private val stage: Stage) : IAdsListener {
    override fun runBeforeAd() {
        Gdx.input.inputProcessor = null
    }

    override fun runAfterAd() {
        screen.hideRestartWindow()
        screen.restartAdsStats()
        world.restart(true)
        Gdx.input.inputProcessor = stage
    }
}
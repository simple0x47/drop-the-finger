package com.elementalg.minigame

/**
 * Mock implementation useful for the debug version.
 *
 * @author Gabriel Amihalachioaie.
 */
class MockAdsBridge : IAdsBridge {
    override fun load() {

    }

    override fun show(listener: IAdsListener) {
        listener.runBeforeAd()
        listener.runAfterAd()
    }
}
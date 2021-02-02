package com.elementalg.minigame

/**
 * Connector of any ads system to the game.
 *
 * @author Gabriel Amihalachioaie.
 */
interface IAdsBridge {
    fun load()
    fun show(listener: IAdsListener)
}
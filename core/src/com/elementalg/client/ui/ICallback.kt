package com.elementalg.client.ui

/**
 * Abstraction of the selected action from a [Notification].
 *
 * @author Gabriel Amihalachioaie.
 */
interface ICallback {
    /**
     * Method called by [Notification] whenever the assigned action was selected.
     */
    fun handle()
}
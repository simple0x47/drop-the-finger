package com.elementalg.client.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.ui.Widget

/**
 * Abstraction of a notification which can be shown on the active screen.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes a basic instance with a [title] and a [message].
 * @param title descriptive heading regarding the notification's purpose.
 * @param message detailed description regardin the notification's purpose.
 */
abstract class Notification protected constructor(private val title: String, private val message: String) : Widget() {

    private var previousInputProcessor: InputProcessor? = null

    protected fun getTitle(): String {
        return title
    }

    protected fun getMessage(): String {
        return message
    }

    /**
     * Stores the previously used [InputProcessor].
     */
    fun show(previousInputProcessor: InputProcessor?) {
        this.previousInputProcessor = previousInputProcessor
    }

    /**
     * Sets as active the previously stored [InputProcessor].
     */
    fun hide() {
        Gdx.input.inputProcessor = previousInputProcessor

        remove()
    }
}
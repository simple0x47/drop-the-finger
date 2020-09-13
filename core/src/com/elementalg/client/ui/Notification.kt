package com.elementalg.client.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.ui.Widget

abstract class Notification protected constructor(private val title: String, private val message: String) : Widget() {

    private var previousInputProcessor: InputProcessor? = null

    protected fun getTitle(): String {
        return title
    }

    protected fun getMessage(): String {
        return message
    }

    fun show(previousInputProcessor: InputProcessor?) {
        this.previousInputProcessor = previousInputProcessor
    }

    fun hide() {
        Gdx.input.inputProcessor = previousInputProcessor

        remove()
    }
}
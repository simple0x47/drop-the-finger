package com.elementalg.minigame

import com.badlogic.gdx.math.Vector2

class Finger(private val radius: Float) {

    private val position: Vector2 = Vector2()

    fun getRadius(): Float {
        return radius
    }

    fun getPosition(): Vector2 {
        return position
    }
}
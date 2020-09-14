package com.elementalg.minigame.obstacles

import com.badlogic.gdx.math.Vector2

class Triangle : IObstacle {
    override fun isWithinObstacle(point: Vector2): Boolean {
        return true
    }
}
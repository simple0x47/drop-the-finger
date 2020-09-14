package com.elementalg.minigame.obstacles

import com.badlogic.gdx.math.Vector2

interface IObstacle {
    fun isWithinObstacle(point: Vector2): Boolean
}
package com.elementalg.minigame.cells

import com.elementalg.minigame.Finger

abstract class Obstacle(size: Float) : Cell(size) {
    abstract fun isWithinObstacle(finger: Finger): Boolean
}
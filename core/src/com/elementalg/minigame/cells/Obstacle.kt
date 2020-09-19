package com.elementalg.minigame.cells

import com.elementalg.minigame.Finger

abstract class Obstacle : Cell() {
    abstract fun isWithinObstacle(finger: Finger): Boolean
}
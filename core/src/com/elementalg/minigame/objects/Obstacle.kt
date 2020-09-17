package com.elementalg.minigame.objects

import com.elementalg.minigame.Cell
import com.elementalg.minigame.Finger

abstract class Obstacle : Cell() {
    abstract fun isWithinObstacle(finger: Finger): Boolean
}
package com.elementalg.minigame.world.cells

import com.elementalg.minigame.world.Finger

abstract class Obstacle(type: Type, size: Float) : Cell(type, size) {
    abstract fun isFingerCollidingWithObstacle(finger: Finger): Boolean
}
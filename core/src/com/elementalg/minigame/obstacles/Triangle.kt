package com.elementalg.minigame.obstacles

import com.elementalg.minigame.Finger

class Triangle : IObstacle {
    override fun isWithinObstacle(finger: Finger): Boolean {
        return true
    }
}
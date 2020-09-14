package com.elementalg.minigame.obstacles

import com.elementalg.minigame.Finger

interface IObstacle {
    fun isWithinObstacle(finger: Finger): Boolean
}
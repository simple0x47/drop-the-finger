package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.Finger
import kotlin.math.abs

class CubeObstacle : Obstacle() {
    override fun isWithinObstacle(finger: Finger): Boolean {
        val center: Vector2 = Vector2(0.5f, 0.5f)
        val fingerPosition: Vector2 = finger.getPosition()

        val pointA: Vector2 = Vector2()
        val pointB: Vector2 = Vector2()

        if (0f <= fingerPosition.x) {
            if (1f >= fingerPosition.y) {
                if (1f >= fingerPosition.x) {
                    if (0f <= fingerPosition.y) {
                        return true
                    } else {
                        pointA.set(1f, 0f)
                        pointB.set(0f, 0f)
                    }
                } else {
                    pointA.set(1f, 1f)
                    pointB.set(1f, 0f)
                }
            } else {
                pointA.set(0f, 1f)
                pointB.set(1f, 1f)
            }
        } else {
            pointA.set(0f, 1f)
            pointB.set(0f, 0f)
        }

        val distance: Float = abs((pointB.y - pointA.y) * fingerPosition.x - (pointB.x - pointA.x) *
                fingerPosition.y + pointB.x * pointA.y - pointB.y * pointA.x) / pointA.dst(pointB)

        return distance <= finger.getRadius()
    }

    override fun draw(batch: Batch) {
        TODO("Not yet implemented")
    }
}
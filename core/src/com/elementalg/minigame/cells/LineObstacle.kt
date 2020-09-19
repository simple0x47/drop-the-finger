package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.Finger
import kotlin.math.abs

class LineObstacle(private val textureRegion: TextureRegion, private val thickness: Float) : Obstacle() {
    private val topLeft: Vector2 = Vector2(0f, 0.5f + (thickness / 2f))
    private val topRight: Vector2 = Vector2(1f, 0.5f + (thickness / 2f))
    private val bottomLeft: Vector2 = Vector2(0f, 0.5f - (thickness / 2f))
    private val bottomRight: Vector2 = Vector2(1f, 0.5f - (thickness / 2f))

    private var angle: Float = 0f

    override fun isWithinObstacle(finger: Finger): Boolean {
        val center: Vector2 = Vector2(0.5f, 0.5f)
        val translatedFinger: Vector2 = Vector2(finger.getPosition()).rotateAround(center, 360f - angle)

        val pointA: Vector2
        val pointB: Vector2

        if (topLeft.x <= translatedFinger.x) {
            if (topLeft.y >= translatedFinger.y) {
                if (bottomRight.x >= translatedFinger.x) {
                    if (bottomRight.y <= translatedFinger.y) {
                        return true
                    } else {
                        pointA = bottomRight
                        pointB = bottomLeft
                    }
                } else {
                    pointA = topRight
                    pointB = bottomRight
                }
            } else {
                pointA = topLeft
                pointB = topRight
            }
        } else {
            pointA = topLeft
            pointB = bottomLeft
        }

        val distance: Float = abs((pointB.y - pointA.y) * translatedFinger.x - (pointB.x - pointA.x) *
                translatedFinger.y + pointB.x * pointA.y - pointB.y * pointA.x) / pointA.dst(pointB)

        return distance <= finger.getRadius()
    }

    override fun draw(batch: Batch) {
        batch.draw(textureRegion, getPosition().x, getPosition().y, 0.5f, 0.5f, getSize(), getSize(),
                1f, 1f, angle, false)
    }
}
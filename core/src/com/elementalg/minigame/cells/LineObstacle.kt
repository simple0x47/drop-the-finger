package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.Finger
import kotlin.math.abs

class LineObstacle(size: Float, private val textureRegion: TextureRegion, private val thickness: Float)
    : Obstacle(size) {
    private val relativeTopLeft: Vector2
    private val relativeTopRight: Vector2
    private val relativeBottomLeft: Vector2
    private val relativeBottomRight: Vector2

    private var angleIncrement: Float = 0f
    private var angle: Float = 0f

    init {
        val top: Float = thickness * size / 2f
        val bottom: Float = top * -1f
        val right: Float = 0.5f * size
        val left: Float = right * -1f

        relativeTopLeft = Vector2(top, left)
        relativeTopRight = Vector2(top, right)
        relativeBottomLeft = Vector2(bottom, left)
        relativeBottomRight = Vector2(bottom, right)
    }

    fun setAngleIncrement(angleIncrement: Float) {
        this.angleIncrement = angleIncrement
    }

    fun getAngleIncrement(): Float {
        return angleIncrement
    }

    override fun isWithinObstacle(finger: Finger): Boolean {
        val translatedFinger: Vector2 = Vector2(finger.getPosition()).rotateAround(getPosition(), 360f - angle)
        translatedFinger.sub(getPosition()) // use line's center as origin point.

        val pointA: Vector2
        val pointB: Vector2

        if (relativeTopLeft.x <= translatedFinger.x) {
            if (relativeTopLeft.y >= translatedFinger.y) {
                if (relativeBottomRight.x >= translatedFinger.x) {
                    if (relativeBottomRight.y <= translatedFinger.y) {
                        return true
                    } else {
                        pointA = relativeBottomRight
                        pointB = relativeBottomLeft
                    }
                } else {
                    pointA = relativeTopRight
                    pointB = relativeBottomRight
                }
            } else {
                pointA = relativeTopLeft
                pointB = relativeTopRight
            }
        } else {
            pointA = relativeTopLeft
            pointB = relativeBottomLeft
        }

        val distance: Float = abs((pointB.y - pointA.y) * translatedFinger.x - (pointB.x - pointA.x) *
                translatedFinger.y + pointB.x * pointA.y - pointB.y * pointA.x) / pointA.dst(pointB)

        return distance <= finger.getRadius()
    }

    override fun draw(batch: Batch) {
        angle = if (angle + angleIncrement > 360f) ((angle + angleIncrement) - 360f) else (angle + angleIncrement)

        batch.draw(textureRegion, getPosition().x, getPosition().y, 0.5f, 0.5f, getSize(),
                getSize() * thickness, 1f, 1f, angle, false)
    }

    companion object {
        const val DEFAULT_THICKNESS: Float = 0.1f
        const val TEXTURE_REGION: String = "LineObstacle"

        private val wallsDefinition: WallsDefinition = WallsDefinition() // no 'obstacles' since it rotates.
    }
}
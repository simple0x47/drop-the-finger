package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.Finger
import kotlin.math.abs
import kotlin.random.Random

class LineObstacle(size: Float, private val textureRegion: TextureRegion, private val thickness: Float)
    : Obstacle(size) {
    private val relativeTopLeft: Vector2
    private val relativeTopRight: Vector2
    private val relativeBottomLeft: Vector2
    private val relativeBottomRight: Vector2

    private val origin: Vector2 = Vector2(getSize() / 2f, getSize() * thickness / 2f)
    private val heightOffset: Float = (getSize() / 2f) - (getSize() * thickness / 2f)

    private var angleIncrement: Float = Random.nextFloat() * MAX_ANGLE_INCREMENT
    private var angle: Float = Random.nextFloat() * MAX_ANGLE

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

    override fun setPosition(position: Vector2) {
        getPosition().set(position.add(0f, heightOffset))
    }

    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y + heightOffset)
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

        batch.draw(textureRegion, getPosition().x, getPosition().y, origin.x, origin.y, getSize(),
                getSize() * thickness, 1f, 1f, angle, false)
    }

    companion object {
        const val MAX_ANGLE_INCREMENT = 5f
        const val MAX_ANGLE = 360f

        const val DEFAULT_THICKNESS: Float = 0.025f
        const val TEXTURE_REGION: String = "Line"
    }
}
package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.Finger
import kotlin.math.abs

class CubeObstacle(size: Float, private val textureRegion: TextureRegion) : Obstacle(size) {
    private val origin: Float = getSize() / 2f

    override fun setPosition(position: Vector2) {
        getPosition().set(position)
    }

    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y)
    }

    override fun isWithinObstacle(finger: Finger): Boolean {
        val fingerPosition: Vector2 = Vector2(finger.getPosition()).sub(getPosition())

        val max: Float = getSize() / 2f
        val min: Float = max * -1f

        val pointA: Vector2 = Vector2()
        val pointB: Vector2 = Vector2()

        if (min <= fingerPosition.x) {
            if (max >= fingerPosition.y) {
                if (max >= fingerPosition.x) {
                    if (min <= fingerPosition.y) {
                        return true
                    } else {
                        pointA.set(max, min)
                        pointB.set(min, min)
                    }
                } else {
                    pointA.set(max, max)
                    pointB.set(max, min)
                }
            } else {
                pointA.set(min, max)
                pointB.set(max, max)
            }
        } else {
            pointA.set(min, max)
            pointB.set(min, min)
        }

        val distance: Float = abs((pointB.y - pointA.y) * fingerPosition.x - (pointB.x - pointA.x) *
                fingerPosition.y + pointB.x * pointA.y - pointB.y * pointA.x) / pointA.dst(pointB)

        return distance <= finger.getRadius()
    }

    override fun draw(batch: Batch) {
        batch.draw(textureRegion, getPosition().x, getPosition().y, origin, origin, getSize(), getSize(), 1f,
                1f, 0f)
    }

    companion object {
        const val TEXTURE_REGION: String = "Cube"
    }
}
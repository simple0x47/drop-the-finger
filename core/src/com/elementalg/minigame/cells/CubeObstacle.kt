package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.Finger
import kotlin.math.abs

class CubeObstacle(size: Float) : Obstacle(size) {
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

    }

    companion object {
        const val TEXTURE_REGION: String = "CubeObstacle"

        val wallsDefinition: WallsDefinition = WallsDefinition()

        init {
            val fullObstructionRange: Vector2 = Vector2(0f, 1f)

            wallsDefinition.addObstruction(WallsDefinition.Position.BOTTOM, fullObstructionRange)
            wallsDefinition.addObstruction(WallsDefinition.Position.RIGHT, fullObstructionRange)
            wallsDefinition.addObstruction(WallsDefinition.Position.TOP, fullObstructionRange)
            wallsDefinition.addObstruction(WallsDefinition.Position.LEFT, fullObstructionRange)
        }
    }
}
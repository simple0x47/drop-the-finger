package com.elementalg.minigame.world.cells

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.world.Finger
import kotlin.math.*
import kotlin.random.Random

class SweeperObstacle(size: Float, private val textureRegion: TextureRegion, private val thickness: Float)
    : Obstacle(Type.SWEEPER, size) {
    private val origin: Vector2 = Vector2(getSize() / 2f, getSize() * thickness / 2f)
    private val heightOffset: Float = (getSize() / 2f) - (getSize() * thickness / 2f)

    private var angleIncrement: Float = Random.nextFloat() * MAX_ANGLE_INCREMENT
    private var angle: Float = Random.nextFloat() * MAX_ANGLE

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

    override fun isFingerCollidingWithObstacle(finger: Finger): Boolean {
        val fingerPosition: Vector2 = Vector2(finger.getPosition()).rotateAround(Vector2(getPosition().x + origin.x,
                getPosition().y + origin.y), 360f - angle)
        val cellPosition: Vector2 = getPosition()

        val firstPoint: Vector2 = Vector2()
        val secondPoint: Vector2 = Vector2()

        if (cellPosition.x <= fingerPosition.x) {
            if (cellPosition.y <= fingerPosition.y) {
                if ((cellPosition.x + getSize()) >= fingerPosition.x) {
                    if ((cellPosition.y + (getSize() * thickness)) >= fingerPosition.y) {
                        return true
                    } else {
                        firstPoint.set(cellPosition.x, cellPosition.y + (getSize() * thickness))
                        secondPoint.set(cellPosition.x + getSize(), cellPosition.y + (getSize() * thickness))
                    }
                } else {
                    firstPoint.set(cellPosition.x + getSize(), cellPosition.y + (getSize() * thickness))
                    secondPoint.set(cellPosition.x + getSize(), cellPosition.y)
                }
            } else {
                firstPoint.set(cellPosition.x, cellPosition.y)
                secondPoint.set(cellPosition.x + getSize(), cellPosition.y)
            }
        } else {
            firstPoint.set(cellPosition.x, cellPosition.y + (getSize() * thickness))
            secondPoint.set(cellPosition.x, cellPosition.y)
        }

        val dist: Float = distanceBetweenSegmentAndPoint(firstPoint, secondPoint, fingerPosition)
        if (dist <= finger.getRadius()) {
            Gdx.app.log("ANGLE", "Angle: $angle")
            Gdx.app.log("FINGER", "Finger no: ${finger.getPosition()}")
            Gdx.app.log("TRANSLATED-FINGER", "Finger: $fingerPosition")
            Gdx.app.log("CENTER", "${Vector2(getPosition().x + origin.x,getPosition().y + origin.y)}")
            Gdx.app.log("SWEEPER-DISTANCE", "FirstPoint: $firstPoint | SecondPoint: $secondPoint = Distance: $dist")
        }
        return dist <= finger.getRadius()
    }

    override fun draw(batch: Batch) {
        angle = if (angle + angleIncrement > 360f) ((angle + angleIncrement) - 360f) else (angle + angleIncrement)

        batch.draw(textureRegion, getPosition().x, getPosition().y, origin.x, origin.y, getSize(),
                getSize() * thickness, 1f, 1f, angle, false)
    }

    private fun rotatePoint(anchor: Vector2, point: Vector2, angle: Float): Vector2 {
        val angleSin: Float = sin(angle * Math.PI / 180f).toFloat()
        val angleCos: Float = cos(angle * Math.PI / 180f).toFloat()

        val relativePoint: Vector2 = Vector2(point).sub(anchor)

        return point.set((relativePoint.x * angleCos - relativePoint.y * angleSin) + anchor.x,
                (relativePoint.x * angleSin + relativePoint.y * angleCos) + anchor.y)
    }

    companion object {
        const val MAX_ANGLE_INCREMENT = 5f
        const val MAX_ANGLE = 360f

        const val DEFAULT_THICKNESS: Float = 0.025f
        const val REQUIRED_SPACE_MARGIN: Float = 0.05f
        const val TEXTURE_REGION: String = "Line"
    }
}
package com.elementalg.minigame.world.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.world.Finger
import kotlin.math.max
import kotlin.random.Random

/**
 * Line, with a percentage [thickness] relative to the cell's size, which can rotate at different angular speeds.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an instance whose thickness is a percentage of the cell's size.
 * @param size cell's side size.
 * @param textureRegion region where the sweeper's texture data is located.
 * @param thickness percentage (0.0, 1.0) of the size of the cell.
 */
class SweeperObstacle(parentCell: CellHolder?, size: Float, private val textureRegion: TextureRegion,
                      private val thickness: Float) : Obstacle(parentCell, Type.SWEEPER, size) {
    private val origin: Vector2 = Vector2(getSize() / 2f, getSize() * thickness / 2f)
    private val heightOffset: Float = (getSize() / 2f) - (getSize() * thickness / 2f)

    private var angleIncrement: Float = max(MIN_ANGLE_INCREMENT, Random.nextFloat() * MAX_ANGLE_INCREMENT)
    private var angle: Float = Random.nextFloat() * MAX_ANGLE

    /**
     * Sets the position of the sweeper accordingly, keeping in mind the existent height offset, which is relative
     * to the sweeper's [thickness].
     *
     * @param position new position.
     */
    override fun setPosition(position: Vector2) {
        getPosition().set(position.add(0f, heightOffset))
    }

    /**
     * Sets the position of the sweeper accordingly, keeping in mind the existent height offset, which is relative
     * to the sweeper's [thickness].
     *
     * @param x new x position.
     * @param y new y position.
     */
    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y + heightOffset)
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

        return dist <= finger.getRadius()
    }

    override fun draw(batch: Batch) {
        angle = if (angle + angleIncrement > 360f) ((angle + angleIncrement) - 360f) else (angle + angleIncrement)

        batch.draw(textureRegion, getPosition().x, getPosition().y, origin.x, origin.y, getSize(),
                getSize() * thickness, 1f, 1f, angle, false)
    }

    companion object {
        const val MIN_ANGLE_INCREMENT: Float = 1.75f
        const val MAX_ANGLE_INCREMENT: Float = 2.5f
        const val MAX_ANGLE: Float = 360f
        const val APPEAR_AFTER_DIFFICULTY: Float = 1f
        const val DEFAULT_THICKNESS: Float = 0.025f
        const val SPACE_MARGIN_REDUCTION: Float = 1.0f
        const val TEXTURE_REGION: String = "Sweeper"
    }
}
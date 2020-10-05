package com.elementalg.minigame.world.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.world.Finger
import com.elementalg.minigame.world.SelfGeneratingWorld
import kotlin.math.*

/**
 * Abstraction of a square-shaped area of space.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor parent instance holding the [Type] of the current cell and it's [size].
 * @param type [Type] of cell.
 * @param size cell's side size.
 */
abstract class Cell(private val type: Type, private val size: Float) {
    enum class Type {
        HOLDER,
        EMPTY,
        SWEEPER,
        SQUARE,
        V,
    }

    private val position: Vector2 = Vector2(0f, 0f)

    fun getType(): Type {
        return type
    }

    /**
     * Gets the position.
     *
     * @return instance of [Vector2] which holds the position using the left bottom as the origin point.
     */
    fun getPosition(): Vector2 {
        return position
    }

    abstract fun setPosition(position: Vector2)
    abstract fun setPosition(x: Float, y: Float)

    /**
     * Gets the size of a side of the cell.
     *
     * @return float containing the size of a side of the cell.
     */
    fun getSize(): Float {
        return size
    }

    /**
     * Returns whether or not the passed [finger] is within the cell.
     *
     * @param finger instance of [Finger] whose position will be checked.
     *
     * @return whether or not [finger] is within the cell.
     */
     fun isFingerWithinCell(finger: Finger): Boolean {
        val fingerPosition: Vector2 = finger.getPosition()
        val cellPosition: Vector2 = getPosition()

        val segmentsFirstPoint: Vector2 = Vector2()
        val segmentsSecondPoint: Vector2 = Vector2()

        if (cellPosition.x <= fingerPosition.x) {
            if (cellPosition.y <= fingerPosition.y) {
                if ((cellPosition.x + getSize()) >= fingerPosition.x) {
                    if ((cellPosition.y + getSize()) >= fingerPosition.y) {
                        return true
                    } else {
                        segmentsFirstPoint.set(cellPosition.x, cellPosition.y + getSize())
                        segmentsSecondPoint.set(cellPosition.x + getSize(), cellPosition.y + getSize())
                    }
                } else {
                    segmentsFirstPoint.set(cellPosition.x + getSize(), cellPosition.y + getSize())
                    segmentsSecondPoint.set(cellPosition.x + getSize(), cellPosition.y)
                }
            } else {
                segmentsFirstPoint.set(cellPosition.x, cellPosition.y)
                segmentsSecondPoint.set(cellPosition.x + getSize(), cellPosition.y)
            }
        } else {
            segmentsFirstPoint.set(cellPosition.x, cellPosition.y + getSize())
            segmentsSecondPoint.set(cellPosition.x, cellPosition.y)
        }

        val dist: Float = distanceBetweenSegmentAndPoint(segmentsFirstPoint, segmentsSecondPoint, fingerPosition)
        return dist <= finger.getRadius()
    }

    /**
     * Returns the distance between a segment defined by two points ([segmentPoint1], [segmentPoint2]) and a [point].
     *
     * @param segmentPoint1 point of the segment.
     * @param segmentPoint2 point of the segment.
     * @param point point whose distance to the segment will be returned.
     *
     * @return distance of the [point] to the segment defined by [segmentPoint1] and [segmentPoint2].
     */
    protected fun distanceBetweenSegmentAndPoint(segmentPoint1: Vector2, segmentPoint2: Vector2,
                                               point: Vector2): Float {
        if ((min(segmentPoint1.x, segmentPoint2.x) <= point.x && point.x <= max(segmentPoint1.x, segmentPoint2.x)) ||
                (min(segmentPoint1.y, segmentPoint2.y) <= point.y && point.y <= max(segmentPoint1.y, segmentPoint2.y))) {

            return abs(((segmentPoint2.y - segmentPoint1.y) * point.x) -
                    ((segmentPoint2.x - segmentPoint1.x) * point.y) +
                    (segmentPoint2.x * segmentPoint1.y) - (segmentPoint2.y * segmentPoint1.x)) /
                    sqrt((((segmentPoint2.y - segmentPoint1.y).pow(2)) +
                            (segmentPoint2.x - segmentPoint1.x).pow(2)))
        } else if (segmentPoint1.x == segmentPoint2.x) {
            val topPoint: Vector2
            val bottomPoint: Vector2

            if (segmentPoint1.y >= segmentPoint2.y) {
                topPoint = segmentPoint1
                bottomPoint = segmentPoint2
            } else {
                topPoint = segmentPoint2
                bottomPoint = segmentPoint1
            }

            return if (bottomPoint.y > point.y) {
                sqrt(((point.y - bottomPoint.y).pow(2)) + ((point.x - bottomPoint.x).pow(2)))
            } else {
                sqrt(((point.y - topPoint.y).pow(2)) + ((point.x - topPoint.x).pow(2)))
            }
        } else {
            val leftPoint: Vector2
            val rightPoint: Vector2

            if (segmentPoint1.x <= segmentPoint2.x) {
                leftPoint = segmentPoint1
                rightPoint = segmentPoint2
            } else {
                leftPoint = segmentPoint2
                rightPoint = segmentPoint1
            }

            return if (leftPoint.x > point.x) {
                sqrt(((point.y - leftPoint.y).pow(2)) + ((point.x - leftPoint.x).pow(2)))
            } else {
                sqrt(((point.y - rightPoint.y).pow(2)) + ((point.x - rightPoint.x).pow(2)))
            }
        }
    }

    /**
     * Returns whether or not the passed number is a power of two.
     *
     * @param number number to be checked.
     *
     * @return whether or not [number] is a power of two.
     */
    private fun isNumberAPowerOfTwo(number: Int): Boolean {
        val root: Float = sqrt(abs(number.toFloat()))

        return (floor(root) - root) == 0.0f
    }

    abstract fun draw(batch: Batch)

    companion object {
        fun createCell(cellType: Type, size: Float, worldAtlas: TextureAtlas): Cell {
            val innerSize: Float = size

            return when (cellType) {
                Type.HOLDER -> {
                    CellHolder(innerSize, worldAtlas,
                            (min(SelfGeneratingWorld.WORLD_SIZE.x, SelfGeneratingWorld.WORLD_SIZE.y) / innerSize).toInt() - 1)
                }
                Type.EMPTY -> {
                    EmptyCell(innerSize)
                }
                Type.SQUARE -> {
                    SquareObstacle(innerSize, worldAtlas.findRegion(SquareObstacle.TEXTURE_REGION))
                }
                Type.SWEEPER -> {
                    SweeperObstacle(innerSize, worldAtlas.findRegion(SweeperObstacle.TEXTURE_REGION),
                            SweeperObstacle.DEFAULT_THICKNESS)
                }
                Type.V -> {
                    VShapedObstacle(innerSize, worldAtlas.findRegion(VShapedObstacle.TEXTURE_REGION))
                }
            }
        }
    }
}
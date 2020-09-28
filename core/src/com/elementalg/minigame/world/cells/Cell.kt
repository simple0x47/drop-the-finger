package com.elementalg.minigame.world.cells

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.world.Finger
import com.elementalg.minigame.world.World
import kotlin.math.*

abstract class Cell(private val type: Type, private val size: Float) {
    enum class Type {
        HOLDER,
        EMPTY,
        SWEEPER,
        CUBE,
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
     * Gets the size of the cell.
     *
     * @return float containing the size.
     */
    fun getSize(): Float {
        return size
    }

    open fun isFingerWithinCell(finger: Finger): Boolean {
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

            if (bottomPoint.y > point.y) {
                return sqrt(((point.y - bottomPoint.y).pow(2)) + ((point.x - bottomPoint.x).pow(2)))
            } else {
                return sqrt(((point.y - topPoint.y).pow(2)) + ((point.x - topPoint.x).pow(2)))
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

            if (leftPoint.x > point.x) {
                return sqrt(((point.y - leftPoint.y).pow(2)) + ((point.x - leftPoint.x).pow(2)))
            } else {
                return sqrt(((point.y - rightPoint.y).pow(2)) + ((point.x - rightPoint.x).pow(2)))
            }
        }
    }

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
                            (min(World.WORLD_SIZE.x, World.WORLD_SIZE.y) / innerSize).toInt() - 1)
                }
                Type.EMPTY -> {
                    EmptyCell(innerSize)
                }
                Type.CUBE -> {
                    CubeObstacle(innerSize, worldAtlas.findRegion(CubeObstacle.TEXTURE_REGION))
                }
                Type.SWEEPER -> {
                    SweeperObstacle(innerSize, worldAtlas.findRegion(SweeperObstacle.TEXTURE_REGION),
                            SweeperObstacle.DEFAULT_THICKNESS)
                }
            }
        }
    }
}
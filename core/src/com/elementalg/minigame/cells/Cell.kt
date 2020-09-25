package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

abstract class Cell(private val size: Float) {
    enum class Type {
        HOLDER,
        EMPTY,
        LINE,
        CUBE,
    }

    private val position: Vector2 = Vector2(0f, 0f)

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

    private fun isNumberAPowerOfTwo(number: Int): Boolean {
        val root: Float = sqrt(abs(number.toFloat()))

        return (floor(root) - root) == 0.0f
    }

    abstract fun draw(batch: Batch)


    companion object {
        const val MAX_SIDE_SIZE: Float = 2f

        fun createCell(cellType: Type, size: Float, worldAtlas: TextureAtlas): Cell {
            val innerSize: Float = size

            return when (cellType) {
                Type.HOLDER -> {
                    CellHolder(innerSize, worldAtlas)
                }
                Type.EMPTY -> {
                    EmptyCell(innerSize)
                }
                Type.CUBE -> {
                    CubeObstacle(innerSize, worldAtlas.findRegion(CubeObstacle.TEXTURE_REGION))
                }
                Type.LINE -> {
                    LineObstacle(innerSize, worldAtlas.findRegion(LineObstacle.TEXTURE_REGION),
                            LineObstacle.DEFAULT_THICKNESS)
                }
            }
        }
    }
}
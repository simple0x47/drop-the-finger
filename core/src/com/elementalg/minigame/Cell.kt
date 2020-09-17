package com.elementalg.minigame

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import kotlin.math.floor
import kotlin.math.sqrt

abstract class Cell {
    private val position: Vector2 = Vector2(0f, 0f)

    private var size: Float = MAX_SIDE_SIZE

    /**
     * Gets the position.
     *
     * @return instance of [Vector2] which holds the position using the center as the origin point.
     */
    fun getPosition(): Vector2 {
        return position
    }

    /**
     * Gets the size of the cell.
     *
     * @return float containing the size.
     */
    fun getSize(): Float {
        return size
    }

    /**
     * Sets the size of the sides of the cell.
     *
     * @param size size of the sides.
     */
    fun setSize(size: Float) {
        this.size = size
    }

    private fun isNumberAPowerOfTwo(number: Int): Boolean {
        val root: Float = sqrt(number.toFloat())

        return (floor(root) - root) == 0.0f
    }

    abstract fun draw(batch: Batch)

    companion object {
        const val MAX_SIDE_SIZE: Float = 2f
    }
}
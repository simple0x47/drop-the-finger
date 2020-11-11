package com.elementalg.minigame.world.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

/**
 * Cell which contains nothing, allowing easier paths.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an instance with each side measuring the passed [size].
 * @param size cell's side size.
 */
class EmptyCell(parentCell: CellHolder?, size: Float) : Cell(parentCell, Type.EMPTY, size) {
    override fun setPosition(position: Vector2) {
        getPosition().set(position)
    }

    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y)
    }

    override fun draw(batch: Batch) {

    }
}
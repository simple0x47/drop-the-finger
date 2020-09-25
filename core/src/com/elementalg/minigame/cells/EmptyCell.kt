package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

class EmptyCell(size: Float) : Cell(size) {
    override fun setPosition(position: Vector2) {
        getPosition().set(position)
    }

    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y)
    }

    override fun draw(batch: Batch) {

    }
}
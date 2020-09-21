package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch

class EmptyCell(size: Float) : Cell(size) {
    override fun draw(batch: Batch) {

    }

    companion object {
        val wallsDefinition: WallsDefinition = WallsDefinition()
    }
}
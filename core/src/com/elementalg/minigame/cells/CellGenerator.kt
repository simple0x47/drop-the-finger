package com.elementalg.minigame.cells

import com.elementalg.minigame.Finger
import kotlin.jvm.Throws

class CellGenerator(private val worldCellHolders: Array<CellHolder>, private val finger: Finger) {

    fun generateInitialCells() {
        worldCellHolders[0].setCells(EmptyCell(), CubeObstacle(), EmptyCell(), CubeObstacle())


    }

    @Throws(IllegalArgumentException::class)
    fun generateCell(cellIndex: Int) {
        require(cellIndex < worldCellHolders.size){"'cellIndex' is not within limits."}


    }

    companion object {
        const val CELL_HOLDER_CHANCE: Float = 0.25f
    }
}
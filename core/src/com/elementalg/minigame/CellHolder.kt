package com.elementalg.minigame

import com.badlogic.gdx.math.Vector2
import kotlin.math.floor

class CellHolder : Cell() {
    private val cells: ArrayList<Cell> = ArrayList(HELD_CELLS)

    /**
     * Sets the 4 cells of the [CellHolder].
     *
     * @param cell0 lower left cell.
     * @param cell1 lower right cell.
     * @param cell2 upper left cell.
     * @param cell3 upper right cell.
     */
    fun setCells(cell0: Cell, cell1: Cell, cell2: Cell, cell3: Cell) {
        cells.clear()

        cells.add(cell0)
        cells.add(cell1)
        cells.add(cell2)
        cells.add(cell3)

        val childSize: Float = getSize() / 4f

        cell0.setSize(childSize)
        cell1.setSize(childSize)
        cell2.setSize(childSize)
        cell3.setSize(childSize)

        updateCellsPosition()
    }

    /**
     * Adds a [Cell] to the [CellHolder]. A total of 4 cells must be added.
     *
     * @param cell object which extends [Cell].
     *
     * @throws IllegalArgumentException if [cell] has already been added once to this [CellHolder].
     * @throws IllegalStateException if [cells] has already reached it's capacity.
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun addCell(cell: Cell) {
        require(!cells.contains(cell)) {"'cell' has already been added once."}
        check(cells.size < HELD_CELLS) {"'cells' has already reached it's capacity."}

        cells.add(cell)

        val childSize: Float = getSize() / 4f

        cell.setSize(childSize)

        updateCellsPosition()
    }

    private fun updateCellsPosition() {
        val holderPosition: Vector2 = getPosition()

        for (cell: Cell in cells) {
            val index: Int = cells.indexOf(cell)

            val signX: Int = if (index % 2 == 0) -1 else 1
            val signY: Int = if (floor(index / 2f) > 0) 1 else -1

            cell.getPosition().set(holderPosition.x + (signX * (cell.getSize() / 2)), holderPosition.y +
                    (signY * (cell.getSize() / 2)))
        }
    }

    companion object {
        const val HELD_CELLS: Int = 4
    }
}
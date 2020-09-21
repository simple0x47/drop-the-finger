package com.elementalg.minigame.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import kotlin.jvm.Throws
import kotlin.math.floor

class CellHolder(size: Float, private val cellsAtlas: TextureAtlas) : Cell(size) {
    private val cells: ArrayList<Cell> = ArrayList(HELD_CELLS)

    init {
        cells.ensureCapacity(HELD_CELLS)
    }

    /**
     * Gets a defined cell.
     *
     * @param position position of the cell to be retrieved. (0 - bottom left) (1 - bottom right) (2 - top left)
     * (3 - top right).
     *
     * @throws IllegalArgumentException if [position] is smaller than 0 and not smaller than [HELD_CELLS].
     */
    @Throws(IllegalArgumentException::class)
    fun getCell(position: Int): Cell {
        require(position in 0 until HELD_CELLS) {"'position' is out of the limits."}

        return cells[position]
    }

    override fun draw(batch: Batch) {
        for (cell: Cell in cells) {
            cell.draw(batch)
        }
    }

    /**
     *
     * Adds a [Cell] to the [CellHolder]. A total of 4 cells must be added.
     *
     * @param cellType [Cell.Type] of [Cell].
     *
     * @throws IllegalStateException if [cells] has already reached it's capacity.
     */
    @Throws(IllegalStateException::class)
    fun addCell(cellType: Type) {
        check(cells.size < HELD_CELLS) {"'cells' is full."}
        val innerSize: Float = getSize() / HELD_CELLS
        val cell: Cell

        cell = when (cellType) {
            Type.HOLDER -> {
                CellHolder(innerSize, cellsAtlas)
            }
            Type.EMPTY -> {
                EmptyCell(innerSize)
            }
            Type.CUBE -> {
                CubeObstacle(innerSize)
            }
            Type.LINE -> {
                LineObstacle(innerSize, cellsAtlas.findRegion(LineObstacle.TEXTURE_REGION),
                        LineObstacle.DEFAULT_THICKNESS)
            }
        }

        cells.add(cell)

        updateCellsPosition()
    }

    /**
     * Adds a [Cell] of the passed [Cell.Type] to the [CellHolder] at desired position.
     * A total of 4 cells must be added.
     *
     * @param cellType [Cell.Type] of [Cell].
     * @param position location of the cell into the [CellHolder]. (0 - bottom left) (1 - bottom right) (2 - top left)
     * (3 - top right).
     *
     * @throws IllegalArgumentException if [position] is less than 0 or not smaller than [HELD_CELLS].
     * @throws IllegalStateException if [cells] has already reached it's capacity.
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun addCell(cellType: Type, position: Int) {
        require(position in 0 until HELD_CELLS) {"'position' is out of the limits."}
        check(cells.size < HELD_CELLS) {"'cells' is full."}

        val innerSize: Float = getSize() / HELD_CELLS
        val cell: Cell

        cell = when (cellType) {
            Type.HOLDER -> {
                CellHolder(innerSize, cellsAtlas)
            }
            Type.EMPTY -> {
                EmptyCell(innerSize)
            }
            Type.CUBE -> {
                CubeObstacle(innerSize)
            }
            Type.LINE -> {
                LineObstacle(innerSize, cellsAtlas.findRegion(LineObstacle.TEXTURE_REGION),
                        LineObstacle.DEFAULT_THICKNESS)
            }
        }

        cells.add(position, cell)

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
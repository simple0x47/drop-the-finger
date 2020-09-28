package com.elementalg.minigame.world.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.world.Finger
import com.elementalg.minigame.world.World
import kotlin.jvm.Throws
import kotlin.math.*

class CellHolder(size: Float, private val worldAtlas: TextureAtlas, private val level: Int) : Cell(Type.HOLDER, size) {
    private val cells: ArrayList<Cell> = ArrayList(HELD_CELLS)

    private var outputCell: Int = -1

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

    /**
     * Returns the level of nesting of the cell holder.
     */
    fun getLevel(): Int {
        return level
    }

    /**
     * Sets the output cell of the CellHolder.
     *
     * @param outputCell integer representing the cell which acts as output.
     */
    @Throws(IllegalArgumentException::class)
    fun setOutputCell(outputCell: Int) {
        require(outputCell in 0 until HELD_CELLS) {"'outputCell' is out of limits."}

        this.outputCell = outputCell
    }

    /**
     * @return integer representing the position of the output cell, -1 if there's no output.
     */
    fun getOutputCell(): Int {
        return outputCell
    }

    override fun setPosition(position: Vector2) {
        getPosition().set(position)

        updateCellsPosition()
    }

    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y)

        updateCellsPosition()
    }

    override fun draw(batch: Batch) {
        for (cell: Cell in cells) {
            cell.draw(batch)
        }
    }

    private fun updateCellsPosition() {
        val holderPosition: Vector2 = getPosition()

        for (cell: Cell in cells) {
            val index: Int = cells.indexOf(cell)

            val multiplierX: Int = if (index % 2 == 0) 0 else 1
            val multiplierY: Int = if (floor(index / 2f) > 0) 1 else 0


            cell.setPosition(holderPosition.x + (multiplierX * cell.getSize()),
                    holderPosition.y + (multiplierY * cell.getSize()))
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
    fun addCell(cellType: Type): Cell {
        check(cells.size < HELD_CELLS) {"'cells' is full."}
        val innerSize: Float = getSize() / 2f
        val cell: Cell = createCell(cellType, innerSize, worldAtlas)

        cells.add(cell)

        updateCellsPosition()

        return cell
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
    fun addCell(cellType: Type, position: Int): Cell {
        require(position in 0 until HELD_CELLS) {"'position' is out of the limits."}
        check(cells.size < HELD_CELLS) {"'cells' is full."}

        val innerSize: Float = getSize() / 2f
        val cell: Cell = createCell(cellType, innerSize, worldAtlas)

        cells.add(position, cell)

        updateCellsPosition()

        return cell
    }

    /**
     * Clears the contained cells.
     */
    fun clear() {
        cells.clear()
    }

    companion object {
        fun getLevelFromSize(size: Float): Int {
            return ((min(World.WORLD_SIZE.x, World.WORLD_SIZE.y) / size) - 1).toInt()
        }

        const val WORLD_CELL_HOLDER_LEVEL: Int = 0
        const val HELD_CELLS: Int = 4
    }
}
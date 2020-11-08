package com.elementalg.minigame.world.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.world.SelfGeneratingWorld
import kotlin.jvm.Throws
import kotlin.math.*

/**
 * Holds 4 different instances of [Cell].
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an empty CellHolder.
 * @param size cell's side size.
 * @param worldAtlas atlas containing the texture coordinates for the world related textures.
 */
class CellHolder(parentCell: CellHolder?, size: Float, private val worldAtlas: TextureAtlas) :
        Cell(parentCell, Type.HOLDER, size) {
    private val cells: ArrayList<Cell> = ArrayList(HELD_CELLS)

    var outputCellPosition: Int = -1
        set(value) {
            if ((value < 0) || (value > HELD_CELLS)) {
                throw IllegalArgumentException("Tried to assign to 'outputCell' an invalid value.")
            }

            field = value
        }
    var inputCellPosition: Int = -1
        set (value) {
            if ((value < 0) || (value > HELD_CELLS)) {
                throw IllegalArgumentException("Tried to assign to 'inputCell' an invalid value.")
            }

            field = value
        }

    var innerRoute: CellContinuousGenerator.Route = CellContinuousGenerator.Route.STRAIGHT

    init {
        cells.ensureCapacity(HELD_CELLS)
    }

    /**
     * Gets a defined cell.
     *
     * @param position position of the cell to be retrieved. (0 - bottom right) (1 - bottom left) (2 - top left)
     * (3 - top right).
     *
     * @return cell at the passed [position].
     *
     * @throws IllegalArgumentException if [position] is smaller than 0 and not smaller than [HELD_CELLS].
     */
    @Throws(IllegalArgumentException::class)
    fun getCell(position: Int): Cell {
        require(position in 0 until HELD_CELLS) {"'position' is out of the limits."}

        return cells[position]
    }

    /**
     * Gets the position of the passed reference to a [Cell].
     * @param innerCell cell whose parent is this instance of [CellHolder].
     *
     * @throws IllegalArgumentException if [innerCell] is not a child of this instance of [CellHolder].
     */
    fun getCellPosition(innerCell: Cell): Int {
        for (i: Int in 0 until HELD_CELLS) {
            if (cells[i] === innerCell) {
                return i
            }
        }

        throw IllegalArgumentException("'innerCell' is not nested within this CellHolder.")
    }

    /**
     * Sets the position of this cell holder and updates its inner cells' positions.
     *
     * @param position new position.
     */
    override fun setPosition(position: Vector2) {
        getPosition().set(position)

        updateInnerCellsPositions()
    }

    /**
     * Sets the position of this cell holder and updates its inner cells' positions.
     *
     * @param x new x position.
     * @param y new y position.
     */
    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y)

        updateInnerCellsPositions()
    }

    /**
     * Draws the inner cells.
     */
    override fun draw(batch: Batch) {
        for (cell: Cell in cells) {
            cell.draw(batch)
        }
    }

    private fun updateInnerCellsPositions() {
        val holderPosition: Vector2 = getPosition()

        for (cell: Cell in cells) {
            val index: Int = cells.indexOf(cell)

            val multiplierX: Int = if ((index == 1) || (index == 2)) 0 else 1
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
     * @throws IllegalStateException if [cells] has reached it's capacity already.
     */
    @Throws(IllegalStateException::class)
    fun addCell(cellType: Type): Cell {
        check(cells.size < HELD_CELLS) {"'cells' is full."}
        val innerSize: Float = getSize() / 2f
        val cell: Cell = createCell(this, cellType, innerSize, worldAtlas)

        cells.add(cell)

        updateInnerCellsPositions()

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

        val innerSize: Float = getSize() / 2f
        val cell: Cell = createCell(this, cellType, innerSize, worldAtlas)

        cells[position] = cell

        updateInnerCellsPositions()

        return cell
    }

    /**
     * Removes all the inner cells.
     */
    fun clear() {
        cells.clear()
    }

    /**
     * Fills with empty cells in order to allow custom adding positioning.
     */
    fun fill() {
        for (i: Int in 0 until HELD_CELLS) {
            addCell(Type.EMPTY)
        }
    }

    companion object {
        fun getLevelFromSize(size: Float): Int {
            return ((min(SelfGeneratingWorld.WORLD_SIZE.x, SelfGeneratingWorld.WORLD_SIZE.y) / size) - 1).toInt()
        }

        const val WORLD_CELL_HOLDER_LEVEL: Int = 0
        const val HELD_CELLS: Int = 4
    }
}
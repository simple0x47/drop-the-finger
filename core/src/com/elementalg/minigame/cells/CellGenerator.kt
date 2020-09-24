package com.elementalg.minigame.cells

import com.elementalg.minigame.World
import kotlin.jvm.Throws
import kotlin.random.Random

class CellGenerator(private val fingerRadius: Float) {
    enum class Route {
        STRAIGHT,
        L_SHAPE,
        C_SHAPE,
    }

    private fun randomObstacleCellType(difficulty: Float): Cell.Type {
        return Cell.Type.CUBE
    }

    private fun randomPassableCellType(isWorldCellHolder: Boolean, canBeCellHolder: Boolean, difficulty: Float):
            Cell.Type {
        val cell: Cell.Type

        val cellHolderChance: Float = if (!canBeCellHolder) 0f else (CELL_HOLDER_CHANCE) + (difficulty * 0.125f)
        val lineObstacleChance: Float = OBSTACLE_CHANCE - cellHolderChance + (difficulty * 0.25f)
        val emptyCellChance: Float = EMPTY_CELL_CHANCE - (difficulty * 0.25f)

        val randomValue: Float = Random.nextFloat()

        if (randomValue <= emptyCellChance) {
            cell = Cell.Type.EMPTY
        } else if (randomValue <= (emptyCellChance + lineObstacleChance)) {
            cell = Cell.Type.LINE
        } else {
            cell = Cell.Type.HOLDER
        }

        return cell
    }

    private fun mustCellBePassable(cellPosition: Int, route: Route, inputPosition: Int, outputPosition: Int): Boolean {
        return if (cellPosition == inputPosition || cellPosition == outputPosition) {
            true
        } else {
            when (route) {
                Route.STRAIGHT -> {
                    false
                }
                Route.C_SHAPE -> {
                    true
                }
                Route.L_SHAPE -> {
                    (cellPosition == (inputPosition + 2))
                }
            }
        }
    }

    /**
     * @param cellHolder Cell holder whose cells must be generated.
     * @param worldCellHolder Whether or not the cell holder is a world one.
     * @param inputPosition Position of the input cell. Must be 0 (bottom left) or 1 (bottom right) if we have a
     * world [cellHolder].
     * @param outputPosition Position of the output cell. Must be 1 (top left) or 2 (top right) if we have a
     * world [cellHolder].
     * @param difficulty Difficulty multiplier, values between 0.0 and 1.0. The greater the value, the greater the
     * possibilities to generate cell holders.
     *
     * @throws IllegalArgumentException if [inputPosition] is not 0 or 1, or if [outputPosition] is not 2 or 3, when
     * [worldCellHolder] is true.
     */
    @Throws(IllegalArgumentException::class)
    fun generateRoute(cellHolder: CellHolder, worldCellHolder: Boolean, inputPosition: Int, outputPosition: Int,
                 difficulty: Float) {
        if (worldCellHolder) {
            require(inputPosition in 0..1) {"'inputPosition' must be 0 or 1 if the cell holder is a world one."}
            require(outputPosition in 2..3) {"'outputPosition' must be 2 or 3 if the cell holder is a world one."}
        }

        cellHolder.clear()

        val route: Route = if ((((inputPosition == 0) && (outputPosition == 3)) ||
                        ((inputPosition == 1) && (outputPosition == 2))) && worldCellHolder) {
                Route.L_SHAPE
            } else {
            val randomForRoute: Float = Random.nextFloat()

            if (randomForRoute <= C_SHAPE_CHANCE + (difficulty * C_SHAPE_CHANCE)) {
                Route.C_SHAPE
            } else {
                Route.STRAIGHT
            }
        }

        val hypotheticalInnerCellHolderSize: Float = cellHolder.getSize() / 4f
        val doesInnerCellHolderSizeComply: Boolean = ((fingerRadius * 2f * World.FINGER_RADIUS_MARGIN) >=
                hypotheticalInnerCellHolderSize)

        for (i: Int in 0 until CellHolder.HELD_CELLS) {
            val cellType: Cell.Type = if (mustCellBePassable(i, route, inputPosition, outputPosition)) {
                val hasACellHolderNextTo: Boolean = if (i > 0) {
                    if (i == 1 || i == 2) {
                        (cellHolder.getCell(0) is CellHolder)
                    } else {
                        ((cellHolder.getCell(1) is CellHolder) || (cellHolder.getCell(2) is CellHolder))
                    }
                } else {
                    false
                }

                val canBeInnerCellHolder: Boolean = (doesInnerCellHolderSizeComply && (!hasACellHolderNextTo) &&
                        (i != inputPosition))

                randomPassableCellType(worldCellHolder, canBeInnerCellHolder, difficulty)
            } else {
                randomObstacleCellType(difficulty)
            }

            val cell: Cell = cellHolder.addCell(cellType, i)

            if (cellType == Cell.Type.HOLDER) {
                generateRoute(cell as CellHolder, false, inputPosition, outputPosition, difficulty)
            }
        }
    }

    companion object {
        const val CELL_HOLDER_CHANCE: Float = 0.25f
        const val OBSTACLE_CHANCE: Float = 0.5f
        const val EMPTY_CELL_CHANCE: Float = 0.5f

        const val C_SHAPE_CHANCE: Float = 0.2f
    }
}
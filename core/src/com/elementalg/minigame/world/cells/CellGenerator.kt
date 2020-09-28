package com.elementalg.minigame.world.cells

import com.elementalg.minigame.world.World
import kotlin.jvm.Throws
import kotlin.math.abs
import kotlin.math.min
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

    private fun randomPassableCellType(canBeCellHolder: Int, canBeSweeper: Int, difficulty: Float):
            Cell.Type {
        val noCellHolder: Int = abs(min(1, canBeCellHolder) - 1)
        val noSweeperObstacle: Int = abs(min(1, canBeSweeper) - 1)

        val cellHolderChance: Float = min(1, canBeCellHolder) * ((CELL_HOLDER_CHANCE +
                (difficulty / 2f * EMPTY_CELL_CHANCE)) + (OBSTACLE_CHANCE * noSweeperObstacle / 2f))

        val sweeperObstacleChance: Float = min(1, canBeSweeper) * (OBSTACLE_CHANCE +
                (difficulty / 2f * EMPTY_CELL_CHANCE) + (noCellHolder * (CELL_HOLDER_CHANCE +
                (difficulty / 2f * EMPTY_CELL_CHANCE))))

        val emptyCellChance: Float = EMPTY_CELL_CHANCE - (difficulty * EMPTY_CELL_CHANCE) +
                (noCellHolder * (CELL_HOLDER_CHANCE + (difficulty / 2f * EMPTY_CELL_CHANCE))) +
                (OBSTACLE_CHANCE * noSweeperObstacle / 2f)

        val randomValue: Float = Random.nextFloat()

        return if (randomValue <= cellHolderChance) {
            Cell.Type.HOLDER
        } else if (randomValue <= (cellHolderChance + sweeperObstacleChance)) {
            Cell.Type.SWEEPER
        } else if (randomValue <= (cellHolderChance + sweeperObstacleChance + emptyCellChance)) {
            Cell.Type.EMPTY
        } else {
            Cell.Type.EMPTY
        }
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
    fun generateRoute(cellHolder: CellHolder, worldCellHolder: Boolean, inputLevel: Int, inputPosition: Int,
                      outputPosition: Int, difficulty: Float) {
        if (worldCellHolder) {
            require(inputPosition in 0..1) {"'inputPosition' must be 0 or 1 if the cell holder is a world one."}
            require(outputPosition in 2..3) {"'outputPosition' must be 2 or 3 if the cell holder is a world one."}
        }

        cellHolder.clear()

        val route: Route = if ((((inputPosition == 0) && (outputPosition == 3)) ||
                        ((inputPosition == 1) && (outputPosition == 2)))) {
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
        val doesInnerCellHolderSizeComply: Boolean = ((fingerRadius * 2f * World.FINGER_RADIUS_MARGIN) <
                hypotheticalInnerCellHolderSize)

        val canBeInnerCellHolder: Int = if (doesInnerCellHolderSizeComply &&
                ((CellHolder.getLevelFromSize(hypotheticalInnerCellHolderSize)) != inputLevel)) 1 else 0

        val hypotheticalSweeperObstacleMargin: Float = (hypotheticalInnerCellHolderSize +
                (hypotheticalInnerCellHolderSize * SweeperObstacle.DEFAULT_THICKNESS) +
                (hypotheticalInnerCellHolderSize * SweeperObstacle.REQUIRED_SPACE_MARGIN))

        val canBeSweeper: Int = if (hypotheticalSweeperObstacleMargin >= (fingerRadius * 2f)) 1 else 0

        for (i: Int in 0 until CellHolder.HELD_CELLS) {
            val cellType: Cell.Type = if (mustCellBePassable(i, route, inputPosition, outputPosition)) {

                randomPassableCellType(canBeInnerCellHolder, canBeSweeper, difficulty)
            } else {
                randomObstacleCellType(difficulty)
            }

            val cell: Cell = cellHolder.addCell(cellType, i)

            if (cellType == Cell.Type.HOLDER) {
                generateRoute(cell as CellHolder, false, inputLevel, inputPosition, outputPosition,
                        difficulty)
            }
        }
    }

    companion object {
        const val CELL_HOLDER_CHANCE: Float = 0.5f
        const val OBSTACLE_CHANCE: Float = 0.25f
        const val EMPTY_CELL_CHANCE: Float = 0.25f

        const val C_SHAPE_CHANCE: Float = 0.2f
    }
}
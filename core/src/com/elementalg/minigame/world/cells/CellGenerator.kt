package com.elementalg.minigame.world.cells

import com.elementalg.minigame.world.SelfGeneratingWorld
import kotlin.jvm.Throws
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

/**
 * Procedural generation of a path based on [Cell].
 *
 * @author Gabriel Amihalachioaie.
 */
class CellGenerator(private val fingerRadius: Float) {
    enum class Route {
        STRAIGHT,
        L_SHAPE,
    }

    /**
     * Returns a [Cell.Type], which is an obstacle, selected <i>randomly</i> by making usage of several factors.
     *
     * @param canBeVShapedObstacle (1) if the obstacle can be [Cell.Type.V] or (0) if not.
     * @param difficulty [SelfGeneratingWorld.difficulty].
     *
     * @return type of obstacle cell selected <i>randomly</i>.
     */
    private fun randomObstacleCellType(canBeVShapedObstacle: Int, difficulty: Float): Cell.Type {
        val reversedCanBeVShapedObstacle: Int = abs(min(1, canBeVShapedObstacle) - 1)

        val vShapedObstacleChance: Float = min(1, canBeVShapedObstacle) *
                (V_SHAPED_OBSTACLE_CHANCE - (difficulty / 2f * V_SHAPED_OBSTACLE_CHANCE))

        val squareObstacleChance: Float = SQUARE_OBSTACLE_CHANCE +
                (min(1, canBeVShapedObstacle) * (difficulty / 2f * V_SHAPED_OBSTACLE_CHANCE)) +
                (reversedCanBeVShapedObstacle * V_SHAPED_OBSTACLE_CHANCE)

        val randomValue: Float = Random.nextFloat()

        return if (randomValue <= vShapedObstacleChance) {
            Cell.Type.V
        } else if (randomValue <= vShapedObstacleChance + squareObstacleChance) {
            Cell.Type.SQUARE
        } else {
            Cell.Type.SQUARE
        }
    }

    /**
     * Returns a [Cell.Type], which is passable, selected <i>randomly</i> by making usage of several factors.
     *
     * @param canBeCellHolder (1) if the cell type can be a [Cell.Type.HOLDER], or (0) if not.
     * @param canBeSweeper (1) if the cell type can be a [Cell.Type.SWEEPER], or (0) if not.
     * @param difficulty [SelfGeneratingWorld.difficulty].
     *
     * @return type of passable cell selected <i>randomly</i>.
     */
    private fun randomPassableCellType(canBeCellHolder: Int, canBeSweeper: Int, difficulty: Float):
            Cell.Type {
        val reversedCanBeCellHolder: Int = abs(min(1, canBeCellHolder) - 1)
        val reversedCanBeSweeper: Int = abs(min(1, canBeSweeper) - 1)

        val cellHolderChance: Float = min(1, canBeCellHolder) * ((CELL_HOLDER_CHANCE +
                (difficulty / 2f * EMPTY_CELL_CHANCE)) + (OBSTACLE_CHANCE * reversedCanBeSweeper / 2f))

        val sweeperObstacleChance: Float = min(1, canBeSweeper) *
                (OBSTACLE_CHANCE + (difficulty / 2f * EMPTY_CELL_CHANCE) +
                        (reversedCanBeCellHolder * (CELL_HOLDER_CHANCE + (difficulty / 2f * EMPTY_CELL_CHANCE))))

        val emptyCellChance: Float = EMPTY_CELL_CHANCE - (difficulty * EMPTY_CELL_CHANCE) +
                (reversedCanBeCellHolder * (CELL_HOLDER_CHANCE + (difficulty / 2f * EMPTY_CELL_CHANCE))) +
                (OBSTACLE_CHANCE * reversedCanBeSweeper / 2f)

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

    /**
     * Indicates whether or not a cell position within a [CellHolder] must be passable.
     *
     * @param cellPosition position within the [CellHolder]. (0 -> bottom left, 1 -> bottom right,
     * 2 -> top left, 3 -> top right)
     * @param route type of [Route].
     * @param inputPosition entering position of the finger inside the [CellHolder].
     * @param outputPosition exiting position of the finger inside the [CellHolder].
     *
     * @return whether or not the cell must be passable.
     */
    private fun mustCellBePassable(cellPosition: Int, route: Route, inputPosition: Int, outputPosition: Int): Boolean {
        return if (cellPosition == inputPosition || cellPosition == outputPosition) {
            true
        } else {
            when (route) {
                Route.STRAIGHT -> {
                    false
                }
                Route.L_SHAPE -> {
                    (cellPosition == (inputPosition + 2))
                }
            }
        }
    }

    /**
     * Generates a route accordingly to the passed arguments.
     *
     * @param cellHolder Cell holder whose cells must be generated.
     * @param worldCellHolder Whether or not the cell holder is a world one.
     * @param inputPosition Position of the input cell. Must be 0 (bottom left) or 1 (bottom right) if we have a
     * world [cellHolder].
     * @param outputPosition Position of the output cell. Must be 1 (top left) or 2 (top right) if we have a
     * world [cellHolder].
     * @param difficulty [SelfGeneratingWorld.difficulty]. Difficulty multiplier, values between 0.0 and 1.0. The greater the value,
     * the greater the possibilities to generate cell holders.
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
            Route.STRAIGHT
        }

        val hypotheticalInnerCellHolderSize: Float = cellHolder.getSize() / 4f
        val doesInnerCellHolderSizeComply: Boolean = ((fingerRadius * 2f * SelfGeneratingWorld.FINGER_RADIUS_MARGIN) <
                hypotheticalInnerCellHolderSize)

        val canBeInnerCellHolder: Int = if (doesInnerCellHolderSizeComply &&
                ((CellHolder.getLevelFromSize(hypotheticalInnerCellHolderSize)) != inputLevel)) 1 else 0

        val hypotheticalSweeperObstacleMargin: Float = (hypotheticalInnerCellHolderSize +
                (hypotheticalInnerCellHolderSize * SweeperObstacle.DEFAULT_THICKNESS) +
                (hypotheticalInnerCellHolderSize * SweeperObstacle.REQUIRED_SPACE_MARGIN))

        val canBeSweeper: Int = if (hypotheticalSweeperObstacleMargin >= (fingerRadius * 2f) &&
                (difficulty >= SweeperObstacle.APPEAR_AFTER_DIFFICULTY)) 1 else 0

        var previousInnerCellType: Cell.Type = Cell.Type.SQUARE // Avoids putting a triangle in the first cell.

        for (i: Int in 0 until CellHolder.HELD_CELLS) {
            val cellType: Cell.Type = if (mustCellBePassable(i, route, inputPosition, outputPosition)) {
                randomPassableCellType(canBeInnerCellHolder, canBeSweeper, difficulty)
            } else {
                val canBeOutlineTriangle: Int = if (previousInnerCellType != Cell.Type.SQUARE &&
                        previousInnerCellType != Cell.Type.V) 0 else 1

                randomObstacleCellType(canBeOutlineTriangle, difficulty)
            }

            previousInnerCellType = cellType

            val cell: Cell = cellHolder.addCell(cellType, i)

            if (cellType == Cell.Type.HOLDER) {
                generateRoute(cell as CellHolder, false, inputLevel, inputPosition, outputPosition,
                        difficulty)
            }
        }
    }

    companion object {
        const val CELL_HOLDER_CHANCE: Float = 0.5f
        const val OBSTACLE_CHANCE: Float = 0.15f
        const val EMPTY_CELL_CHANCE: Float = 0.35f

        const val SQUARE_OBSTACLE_CHANCE: Float = 0.5f
        const val V_SHAPED_OBSTACLE_CHANCE: Float = 0.5f

        const val C_SHAPE_CHANCE: Float = 0.2f
    }
}
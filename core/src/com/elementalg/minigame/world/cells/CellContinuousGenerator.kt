package com.elementalg.minigame.world.cells

import com.elementalg.minigame.world.SelfGeneratingWorld
import kotlin.jvm.Throws
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

/**
 * Improved cell generator.
 *
 * @author Gabriel Amihalachioaie.
 */
class CellContinuousGenerator(private val fingerRadius: Float) {
    enum class Route {
        STRAIGHT,
        L_SHAPE_UP,
        L_SHAPE_DOWN,
        POINT,
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
     *
     * @return whether or not the cell must be passable.
     */
    private fun canBeCellPositionAnObstacle(cellHolderBeingGenerated: CellHolder, cellPosition: Int,
                                            route: Route): Boolean {
        return if ((cellPosition == cellHolderBeingGenerated.inputCellPosition) ||
                (cellPosition == cellHolderBeingGenerated.outputCellPosition)) {
            false
        } else { // Bridge cells.
            when (route) {
                Route.STRAIGHT -> {
                    true
                }
                Route.L_SHAPE_UP -> {
                    if (cellHolderBeingGenerated.inputCellPosition == 0) {
                        (cellPosition != 3)
                    } else if (cellHolderBeingGenerated.inputCellPosition == 1) {
                        (cellPosition != 2)
                    } else {
                        true
                    }
                }
                Route.L_SHAPE_DOWN -> {
                    if (cellHolderBeingGenerated.inputCellPosition == 0) {
                        (cellPosition != 1)
                    } else if (cellHolderBeingGenerated.inputCellPosition == 1) {
                        (cellPosition != 0)
                    } else {
                        true
                    }
                }
                Route.POINT -> {
                    true
                }
            }
        }
    }

    private fun retrieveInputCellPosition(cellHolderBeingGenerated: CellHolder,
                                          previousCell: Cell): Int {
        if ((cellHolderBeingGenerated.level != previousCell.level) && (previousCell is CellHolder)) {
            throw IllegalArgumentException("Cell hodlers must have the same level at " +
                    "'retrieveInputCellPosition'.")
        }

        if ((cellHolderBeingGenerated.level == 0) && (previousCell.level == 0)) {
            if (previousCell !is CellHolder) {
                throw IllegalStateException("Detected level 0 cell without being a CellHolder.")
            }

            return abs(previousCell.outputCellPosition - 3)
        } else {
            val cellParent: CellHolder? = cellHolderBeingGenerated.getParent()
            val previousParent: CellHolder? = previousCell.getParent()

            if ((cellParent != null) && (previousParent != null)) {
                if (cellParent === previousParent) {
                    val cellPosition: Int = cellParent.getCellPosition(cellHolderBeingGenerated)
                    val previousPosition: Int = cellParent.getCellPosition(previousCell)

                    if (previousCell is CellHolder) {
                        if (((cellPosition == 0) && (previousPosition == 1)) ||
                                ((cellPosition == 3) && (previousPosition == 2))) {
                            if (previousCell.outputCellPosition == 0) {
                                return 1
                            } else if (previousCell.outputCellPosition == 3) {
                                return 2
                            } else {
                                throw IllegalStateException("Previous cell is not connected.")
                            }
                        } else if (((cellPosition == 1) && (previousPosition == 0)) ||
                                ((cellPosition == 2) && (previousPosition == 3))) {
                            if (previousCell.outputCellPosition == 1) {
                                return 0
                            } else if (previousCell.outputCellPosition == 2) {
                                return 3
                            } else {
                                throw IllegalStateException("Previous cell is not connected.")
                            }
                        } else if (((cellPosition == 3) && (previousPosition == 0)) ||
                                ((cellPosition == 2) && (previousPosition == 1))) {
                            if (previousCell.outputCellPosition == 3) {
                                return 0
                            } else if (previousCell.outputCellPosition == 2) {
                                return 1
                            } else {
                                throw IllegalStateException("Previous cell is not connected.")
                            }
                        } else {
                            throw IllegalStateException("Movement cannot go downwards.")
                        }
                    } else {
                        if (((cellPosition == 0) && (previousPosition == 1)) ||
                                ((cellPosition == 3) && (previousPosition == 2))) {
                            return if (Random.nextBoolean()) 1 else 2
                        } else if (((cellPosition == 1) && (previousPosition == 0)) ||
                                ((cellPosition == 2) && (previousPosition == 3))) {
                            return if (Random.nextBoolean()) 0 else 3
                        } else if (((cellPosition == 3) && (previousPosition == 0)) ||
                                ((cellPosition == 2) && (previousPosition == 1))) {
                            return if (Random.nextBoolean()) 0 else 1
                        } else {
                            throw IllegalStateException("Movement cannot go downwards.")
                        }
                    }
                } else {
                    if (cellHolderBeingGenerated.level <= 1) {
                        if (previousCell is CellHolder) {
                            return abs(previousCell.outputCellPosition - 3)
                        } else {
                            return if (Random.nextBoolean()) 0 else 1
                        }
                    } else {
                        val cellPosition: Int = cellParent.getCellPosition(cellHolderBeingGenerated)
                        val previousPosition: Int = previousParent.getCellPosition(previousCell)

                        if (previousCell is CellHolder) {
                            if (((cellPosition == 0) && (previousPosition == 1)) ||
                                    ((cellPosition == 3) && (previousPosition == 2))) {
                                if (previousCell.outputCellPosition == 2) {
                                    return 3
                                } else if (previousCell.outputCellPosition == 1) {
                                    return 0
                                } else {
                                    throw IllegalStateException("Previous cell is not connected.")
                                }
                            } else if (((cellPosition == 1) && (previousPosition == 0)) ||
                                    ((cellPosition == 2) && (previousPosition == 3))) {
                                if (previousCell.outputCellPosition == 3) {
                                    return 2
                                } else if (previousCell.outputCellPosition == 0) {
                                    return 1
                                } else {
                                    throw IllegalStateException("Previous cell is not connected.")
                                }
                            } else if (((cellPosition == 0) && (previousPosition == 3)) ||
                                    ((cellPosition == 1) && (previousPosition == 2))) {
                                if (previousCell.outputCellPosition == 3) {
                                    return 0
                                } else if (previousCell.outputCellPosition == 2) {
                                    return 1
                                } else {
                                    throw IllegalStateException("Previous cell is not connected.")
                                }
                            } else {
                                throw IllegalStateException("Movement cannot go downwards.")
                            }
                        } else {
                            if (((cellPosition == 0) && (previousPosition == 1)) ||
                                    ((cellPosition == 3) && (previousPosition == 2))) {
                                return if (Random.nextBoolean()) 3 else 0
                            } else if (((cellPosition == 1) && (previousPosition == 0)) ||
                                    ((cellPosition == 2) && (previousPosition == 3))) {
                                return if (Random.nextBoolean()) 2 else 1
                            } else if (((cellPosition == 0) && (previousPosition == 3)) ||
                                    ((cellPosition == 1) && (previousPosition == 2))) {
                                return if (Random.nextBoolean()) 0 else 1
                            } else {
                                throw IllegalStateException("Movement cannot go downwards.")
                            }
                        }
                    }
                }
            } else {
                throw IllegalStateException("Null parents, although level is not 0.")
            }
        }
    }

    /**
     * Generates a 'random' output cell position for the passed [CellHolder].
     *
     * @param cellHolderBeingGenerated cell holder which is being generated at the moment, must have an input cell
     * already defined.
     *
     * @return int representing the part of the cell holder used as exit point for the finger pointer.
     */
    private fun generateOutputCellPosition(cellHolderBeingGenerated: CellHolder): Int {
        if (cellHolderBeingGenerated.level > 0) {
            val parentCell: CellHolder = cellHolderBeingGenerated.getParent()
                    ?: throw IllegalStateException("Null parent, although level is not 0.")

            when (val innerCellPosition: Int = parentCell.getCellPosition(cellHolderBeingGenerated)) {
                parentCell.inputCellPosition -> {
                    if (parentCell.innerRoute == Route.L_SHAPE_UP) {
                        if (((innerCellPosition == 0) && (parentCell.outputCellPosition == 2)) ||
                                ((innerCellPosition == 1) && (parentCell.outputCellPosition == 3))) {
                            return if (Random.nextBoolean()) 2 else 3
                        } else {
                            throw IllegalStateException("Invalid values for 'L_SHAPE_UP' route.")
                        }
                    } else if (parentCell.innerRoute == Route.L_SHAPE_DOWN) {
                        if ((innerCellPosition == 0) && (parentCell.outputCellPosition == 2)) {
                            return 2
                        } else if ((innerCellPosition == 1) && (parentCell.outputCellPosition == 3)) {
                            return 3
                        } else {
                            throw IllegalStateException("Invalid values for 'L_SHAPE_DOWN' route.")
                        }
                    } else {
                        return if (Random.nextBoolean()) 2 else 3
                    }
                }
                parentCell.outputCellPosition -> {
                    return if (Random.nextBoolean()) 2 else 3
                }
                else -> {
                    if (((innerCellPosition == 0) && (parentCell.outputCellPosition == 3)) ||
                            (((innerCellPosition == 1) && (parentCell.outputCellPosition == 2)))) {
                        return if (Random.nextBoolean()) 2 else 3
                    } else if ((innerCellPosition == 2) && (parentCell.outputCellPosition == 3)) {
                        return 3
                    } else if ((innerCellPosition == 3) && (parentCell.outputCellPosition == 2)) {
                        return 2
                    } else {
                        throw IllegalStateException("Movement cannot go downwards.")
                    }
                }
            }
        } else {
            return if (Random.nextBoolean()) 2 else 3
        }
    }

    fun generateRouteType(cellHolderBeingGenerated: CellHolder): Route {
        return if (((cellHolderBeingGenerated.inputCellPosition == 0) &&
                        (cellHolderBeingGenerated.outputCellPosition == 1)) ||
                ((cellHolderBeingGenerated.inputCellPosition == 1) &&
                        (cellHolderBeingGenerated.outputCellPosition == 0)) ||
                ((cellHolderBeingGenerated.inputCellPosition == 0) &&
                        (cellHolderBeingGenerated.outputCellPosition == 3)) ||
                ((cellHolderBeingGenerated.inputCellPosition == 1) &&
                        (cellHolderBeingGenerated.outputCellPosition == 2)) ||
                ((cellHolderBeingGenerated.inputCellPosition == 2) &&
                        (cellHolderBeingGenerated.outputCellPosition == 3)) ||
                ((cellHolderBeingGenerated.inputCellPosition == 3) &&
                        (cellHolderBeingGenerated.outputCellPosition == 2))) {
            Route.STRAIGHT
        } else if (((cellHolderBeingGenerated.inputCellPosition == 0) &&
                        (cellHolderBeingGenerated.outputCellPosition == 2)) ||
                ((cellHolderBeingGenerated.inputCellPosition == 1) &&
                        (cellHolderBeingGenerated.outputCellPosition == 3))) {
            if (Random.nextBoolean()) Route.L_SHAPE_DOWN else Route.L_SHAPE_UP
        } else if (((cellHolderBeingGenerated.inputCellPosition == 2) &&
                        ((cellHolderBeingGenerated.outputCellPosition == 2))) ||
                ((cellHolderBeingGenerated.inputCellPosition == 3) &&
                        ((cellHolderBeingGenerated.outputCellPosition == 3)))) {
            Route.POINT
        } else {
            throw IllegalStateException("Movement cannot go downwards.")
        }
    }

    /**
     * Generates a route accordingly to the passed arguments.
     * @param cellHolderBeingGenerated Instance of [CellHolder] whose content is getting generated.
     * @param previousCell Instance of [Cell] of the same nesting level.
     *
     * @throws IllegalArgumentException if [cellHolderBeingGenerated] has not the same nesting level as [previousCell].
     */
    @Throws(IllegalArgumentException::class)
    fun generateRoute(cellHolderBeingGenerated: CellHolder, previousCell: Cell, difficulty: Float) {
        if ((cellHolderBeingGenerated.level != previousCell.level) && (previousCell is CellHolder)) {
            throw IllegalArgumentException("'cellHolderBeingGenerated' is has not the same nesting level as " +
                    "'previousCell': ${cellHolderBeingGenerated.level} - ${previousCell.level}")
        }

        cellHolderBeingGenerated.clear()
        cellHolderBeingGenerated.fill()

        cellHolderBeingGenerated.inputCellPosition = retrieveInputCellPosition(cellHolderBeingGenerated,
                previousCell)
        cellHolderBeingGenerated.outputCellPosition = generateOutputCellPosition(cellHolderBeingGenerated)

        val route: Route = generateRouteType(cellHolderBeingGenerated)
        cellHolderBeingGenerated.innerRoute = route

        val hypotheticalInnerCellHolderSize: Float = cellHolderBeingGenerated.getSize() / 4f
        val doesInnerCellHolderSizeComply: Boolean = ((fingerRadius * 2f * SelfGeneratingWorld.FINGER_RADIUS_MARGIN) <
                hypotheticalInnerCellHolderSize)

        val canBeInnerCellHolder: Int = if ((doesInnerCellHolderSizeComply) &&
                (difficulty >= CellHolder.APPEAR_AFTER_DIFFICULTY)) 1 else 0

        val hypotheticalSweeperObstacleMargin: Float = (hypotheticalInnerCellHolderSize -
                (hypotheticalInnerCellHolderSize * SweeperObstacle.DEFAULT_THICKNESS) -
                (hypotheticalInnerCellHolderSize * SweeperObstacle.REQUIRED_SPACE_MARGIN))

        var canBeSweeper: Int = if (hypotheticalSweeperObstacleMargin >= (fingerRadius * 2f) &&
                (abs(difficulty - SweeperObstacle.APPEAR_AFTER_DIFFICULTY) < 0.01f)) 1 else 0

        val canBeVShapedObstacle: Int = if (Random.nextBoolean()) 0 else 1

        var innerPreviousPassableCell: Cell = if (previousCell is CellHolder) {
            previousCell.getCell(previousCell.outputCellPosition)
        } else {
            previousCell
        }

        // Input cell creation.

        var lastCellType: Cell.Type = randomPassableCellType(canBeInnerCellHolder, canBeSweeper, difficulty)
        val inputCell: Cell = cellHolderBeingGenerated.addCell(lastCellType,
                cellHolderBeingGenerated.inputCellPosition)

        if (lastCellType == Cell.Type.HOLDER) {
            generateRoute(inputCell as CellHolder, innerPreviousPassableCell, difficulty)
        } else if (lastCellType == Cell.Type.SWEEPER) {
            canBeSweeper = 0
        }

        innerPreviousPassableCell = inputCell

        // Middle cell creation.

        for (i: Int in 0 until CellHolder.HELD_CELLS) {
            if ((i != cellHolderBeingGenerated.inputCellPosition) &&
                    (i != cellHolderBeingGenerated.outputCellPosition)) {
                if (canBeCellPositionAnObstacle(cellHolderBeingGenerated, i, route)) {
                    lastCellType = randomObstacleCellType(canBeVShapedObstacle, difficulty)
                    cellHolderBeingGenerated.addCell(lastCellType, i)
                } else {
                    lastCellType = randomPassableCellType(canBeInnerCellHolder, canBeSweeper, difficulty)
                    val cell: Cell = cellHolderBeingGenerated.addCell(lastCellType, i)

                    if (lastCellType == Cell.Type.HOLDER) {
                        generateRoute(cell as CellHolder, innerPreviousPassableCell, difficulty)
                    }

                    innerPreviousPassableCell = cell
                }
            }
        }

        lastCellType = randomPassableCellType(canBeInnerCellHolder, canBeSweeper, difficulty)
        val outputCell: Cell = cellHolderBeingGenerated.addCell(lastCellType,
                cellHolderBeingGenerated.outputCellPosition)

        if (lastCellType == Cell.Type.HOLDER) {
            generateRoute(outputCell as CellHolder, innerPreviousPassableCell, difficulty)
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
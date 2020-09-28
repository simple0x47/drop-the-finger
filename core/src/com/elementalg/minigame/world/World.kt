package com.elementalg.minigame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.elementalg.client.managers.DependencyManager
import com.elementalg.minigame.world.cells.Cell
import com.elementalg.minigame.world.cells.CellGenerator
import com.elementalg.minigame.world.cells.CellHolder
import com.elementalg.minigame.world.cells.Obstacle
import kotlin.jvm.Throws
import kotlin.random.Random

class World(private val stage: Stage, private val worldViewport: Viewport) {
    private val cellHolders: ArrayList<CellHolder> = ArrayList(CELL_HOLDERS)

    private var speed: Float = 0.05f
    private var difficulty: Float = 0f
    private var started: Boolean = false

    private lateinit var finger: Finger
    private lateinit var fingerListener: FingerListener
    private lateinit var cellGenerator: CellGenerator

    /**
     * Adds a finger to the world with the passed [fingerRadius].
     *
     * @param fingerRadius radius of the finger in pixels.
     *
     * @throws IllegalStateException if [finger] has been initialized already.
     */
    @Throws(IllegalStateException::class)
    private fun initializeFinger(worldAtlas: TextureAtlas, fingerRadius: Float) {
        check(!this::finger.isInitialized) {"'finger' has already been initialized once."}

        finger = Finger(this, worldViewport, worldAtlas.findRegion("Finger"),
                fingerRadius / UNIT_TO_PIXELS)
        cellGenerator = CellGenerator(finger.getRadius())
    }

    private fun initializeStartingCellHolders() {
        cellHolders[0].addCell(Cell.Type.EMPTY, 0)
        cellHolders[0].addCell(Cell.Type.EMPTY, 1)
        cellHolders[0].addCell(Cell.Type.EMPTY, 2)
        cellHolders[0].addCell(Cell.Type.EMPTY, 3)

        cellHolders[0].setOutputCell(Random.nextInt(2, 4))

        generateWorldCellHolder(1)
        generateWorldCellHolder(2)
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun generateWorldCellHolder(cellHolderIndex: Int) {
        require(cellHolderIndex in 0 until CELL_HOLDERS) {"'cellHolderIndex' is out of limits."}
        check(this::cellGenerator.isInitialized) {"'cellGenerator' has not been initialized yet."}

        val inputCellHolder: CellHolder = if (cellHolderIndex > 0) cellHolders[cellHolderIndex - 1]
            else cellHolders[CELL_HOLDERS - 1]

        val inputPosition: Int = inputCellHolder.getOutputCell() - (CellHolder.HELD_CELLS / 2)
        val outputPosition: Int = Random.nextInt(2, 4)

        check(inputPosition in 0..1){"'inputCellPosition' is not a bottom cell."}

        cellHolders[cellHolderIndex].setOutputCell(outputPosition)
        cellGenerator.generateRoute(cellHolders[cellHolderIndex], true, CellHolder.WORLD_CELL_HOLDER_LEVEL,
                inputPosition, outputPosition, difficulty)
    }

    @Throws(IllegalStateException::class)
    fun create(dependencyManager: DependencyManager, fingerRadius: Float) {
        val assets: HashMap<String, Any> = dependencyManager.retrieveAssets("WORLD")

        check (assets.containsKey("WorldAtlas")) {"World dependency 'CellsAtlas' is not solved."}

        val worldAtlas: TextureAtlas = assets["WorldAtlas"] as TextureAtlas

        val worldCellHolderSize: Float = WORLD_SIZE.x

        for (i: Int in 0 until CELL_HOLDERS) {
            val cellHolder: CellHolder = CellHolder(worldCellHolderSize, worldAtlas, CellHolder.WORLD_CELL_HOLDER_LEVEL)

            cellHolder.getPosition().set(0f, (i * worldCellHolderSize))

            cellHolders.add(cellHolder)
        }

        initializeFinger(worldAtlas, fingerRadius)
        initializeStartingCellHolders()
    }

    fun render(batch: Batch) {
        finger.draw(batch)

        var count: Int = 0
        for (cellHolder: CellHolder in cellHolders) {
            cellHolder.draw(batch)

            if (started) {
                displaceCellHolder(cellHolder)

                if (count < 2) {
                    if (cellHolder.isFingerWithinCell(finger)) {
                        if (isCollidingFingerWithCellHoldersInnerObstacles(cellHolder, finger)) {
                            gameOver()
                        } else {
                            ++count
                        }
                    }
                }
            }
        }
    }

    private fun isCollidingFingerWithCellHoldersInnerObstacles(cellHolder: CellHolder, finger: Finger): Boolean {
        for (cellIndex: Int in 0 until CellHolder.HELD_CELLS) {
            val cell: Cell = cellHolder.getCell(cellIndex)

            if (cell.getType() == Cell.Type.HOLDER) {
                if (isCollidingFingerWithCellHoldersInnerObstacles(cell as CellHolder, finger)) {
                    Gdx.app.log("COLLISION", "CELL HOLDER")
                    return true
                }
            } else if (cell is Obstacle) {
                if (cell.isFingerCollidingWithObstacle(finger)) {
                    Gdx.app.log("COLLISION", "CellType: ${cell.getType()} | CellSize: ${cell.getSize()} | CellPosition: ${cell.getPosition()} | FingerPosition: ${finger.getPosition()} | FingerRadius: ${finger.getRadius()}")
                    return true
                }
            }
        }

        return false
    }

    private fun displaceCellHolder(cellHolder: CellHolder) {
        cellHolder.setPosition(cellHolder.getPosition().sub(0f, speed))

        if (cellHolder.getPosition().y <= ( -1 * WORLD_SIZE.x)) { // if it's under the screen
            generateWorldCellHolder(cellHolders.indexOf(cellHolder))

            cellHolder.setPosition(cellHolder.getPosition().x,  WORLD_SIZE.x * 2f)
        }
    }

    fun show() {
        fingerListener = FingerListener(finger, this)

        stage.addListener(fingerListener)
    }

    fun start() {
        started = true
    }

    fun isStarted(): Boolean {
        return started
    }

    fun gameOver() {
        Gdx.app.log("LISTENER", "REMOVED")
        stage.removeListener(fingerListener)

        started = false
    }

    fun dispose() {

    }

    companion object {
        const val CELL_HOLDERS: Int = 3
        const val UNIT_TO_PIXELS: Int = 100
        const val FINGER_RADIUS_MARGIN: Float = 1.5f // lower = harder *evil laugh*, but never lower than 1.
        const val MAX_SPEED: Float = 0.05f

        val WORLD_SIZE: Vector2 = Vector2(8f, 16f)
    }
}
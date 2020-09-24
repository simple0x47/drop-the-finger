package com.elementalg.minigame

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.elementalg.client.managers.DependencyManager
import com.elementalg.minigame.cells.Cell
import com.elementalg.minigame.cells.CellGenerator
import com.elementalg.minigame.cells.CellHolder
import kotlin.jvm.Throws
import kotlin.random.Random

class World {
    private val cellHolders: ArrayList<CellHolder> = ArrayList(CELL_HOLDERS)

    private var difficulty: Float = 0f

    private lateinit var finger: Finger
    private lateinit var cellGenerator: CellGenerator

    /**
     * Adds a finger to the world with the passed [fingerRadius].
     *
     * @param fingerRadius radius of the finger in pixels.
     *
     * @throws IllegalStateException if [finger] has been initialized already.
     */
    @Throws(IllegalStateException::class)
    private fun initializeFinger(fingerRadius: Float) {
        check(!this::finger.isInitialized) {"'finger' has already been added once."}

        finger = Finger(fingerRadius / UNIT_TO_PIXELS)
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
        cellGenerator.generateRoute(cellHolders[cellHolderIndex], true, inputPosition,
                outputPosition, difficulty)
    }

    @Throws(IllegalStateException::class)
    fun create(dependencyManager: DependencyManager, fingerRadius: Float) {
        val assets: HashMap<String, Any> = dependencyManager.retrieveAssets("WORLD")

        check (assets.containsKey("CellsAtlas")) {"World dependency 'CellsAtlas' is not solved."}

        val cellsAtlas: TextureAtlas = assets["CellsAtlas"] as TextureAtlas

        val worldCellHolderSize: Float = WORLD_SIZE / 2f;

        for (i: Int in 0 until CELL_HOLDERS) {
            cellHolders.add(CellHolder(worldCellHolderSize, cellsAtlas))
        }

        initializeFinger(fingerRadius)
        initializeStartingCellHolders()
    }

    fun draw(batch: Batch) {
        for (cellHolder: CellHolder in cellHolders) {
            cellHolder.draw(batch)
        }
    }

    fun dispose() {

    }

    companion object {
        const val CELL_HOLDERS: Int = 3
        const val UNIT_TO_PIXELS: Int = 100
        const val WORLD_SIZE: Float = 16f
        const val FINGER_RADIUS_MARGIN: Float = 1.25f // lower = harder *evil laugh*, but never lower than 1.
    }
}
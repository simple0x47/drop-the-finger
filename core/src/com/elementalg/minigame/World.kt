package com.elementalg.minigame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.client.managers.DependencyManager
import com.elementalg.minigame.cells.Cell
import com.elementalg.minigame.cells.CellGenerator
import com.elementalg.minigame.cells.CellHolder
import kotlin.jvm.Throws
import kotlin.random.Random

class World {
    private val cellHolders: ArrayList<CellHolder> = ArrayList(CELL_HOLDERS)

    private var speed: Float = 0.1f
    private var difficulty: Float = 0f

    private lateinit var finger: Finger
    private lateinit var cellGenerator: CellGenerator

    private lateinit var textureRegion: TextureRegion

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

        Gdx.app.log("RADIUS", "FingerRadius: ${fingerRadius / UNIT_TO_PIXELS}")
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

        check (assets.containsKey("WorldAtlas")) {"World dependency 'CellsAtlas' is not solved."}

        val worldAtlas: TextureAtlas = assets["WorldAtlas"] as TextureAtlas

        val worldCellHolderSize: Float = WORLD_SIZE.x

        for (i: Int in 0 until CELL_HOLDERS) {
            val cellHolder: CellHolder = CellHolder(worldCellHolderSize, worldAtlas)

            cellHolder.getPosition().set(0f, (i * worldCellHolderSize))

            cellHolders.add(cellHolder)
        }

        initializeFinger(fingerRadius)
        initializeStartingCellHolders()

        textureRegion = worldAtlas.findRegion("FingerPointerTest")
    }

    fun draw(batch: Batch) {
        for (cellHolder: CellHolder in cellHolders) {
            cellHolder.draw(batch)

            displaceCellHolder(cellHolder)
        }

        batch.draw(textureRegion, 1f, 1f, finger.getRadius(), finger.getRadius())
    }

    private fun displaceCellHolder(cellHolder: CellHolder) {
        cellHolder.setPosition(cellHolder.getPosition().sub(0f, speed))

        if (cellHolder.getPosition().y <= ( -1 * WORLD_SIZE.x)) { // if it's under the screen
            generateWorldCellHolder(cellHolders.indexOf(cellHolder))

            cellHolder.setPosition(cellHolder.getPosition().x,  WORLD_SIZE.x * 2f)
        }
    }

    fun dispose() {

    }

    companion object {
        const val CELL_HOLDERS: Int = 3
        const val UNIT_TO_PIXELS: Int = 100
        const val FINGER_RADIUS_MARGIN: Float = 1.25f // lower = harder *evil laugh*, but never lower than 1.
        const val MAX_SPEED: Float = 0.2f

        val WORLD_SIZE: Vector2 = Vector2(8f, 16f)
    }
}
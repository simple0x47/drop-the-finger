package com.elementalg.minigame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
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
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.random.Random

/**
 * Space in which the game takes place. It handles the different aspects of the game, such as, speed, difficulty,
 * score, actor movement...
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an instance with the passed parameters.
 * @param stage LibGDX's stage instance instance used at the parent screen.
 * @param worldViewport actor's viewports instance used at the parent screen.
 */
class World(private val stage: Stage, private val worldViewport: Viewport) {
    private val cellHolders: ArrayList<CellHolder> = ArrayList(CELL_HOLDERS)

    private var speed: Float = 0.025f
    private var difficulty: Float = 0f
    private var started: Boolean = false
    private var score: Float = 0f

    private lateinit var worldAtlas: TextureAtlas

    private lateinit var worldBackground: Texture

    private lateinit var finger: Finger
    private lateinit var fingerListener: FingerListener
    private lateinit var cellGenerator: CellGenerator

    /**
     * Initializes a finger to the world with the passed [fingerRadius].
     *
     * @param worldAtlas atlas containing the different texture regions required for the world's actors.
     * @param fingerRadius radius of the finger in pixels.
     *
     * @throws IllegalStateException if [finger] has been initialized already.
     */
    @Throws(IllegalStateException::class)
    private fun initializeFinger(worldAtlas: TextureAtlas, fingerRadius: Float) {
        check(!this::finger.isInitialized) {"'finger' has already been initialized once."}

        finger = Finger(worldAtlas, this, worldViewport, fingerRadius / UNIT_TO_PIXELS)
        cellGenerator = CellGenerator(finger.getRadius())
    }

    /**
     * Initializes the world cell holders for the start of the game.
     */
    private fun initializeWorldCellHolders() {

        val worldCellHolderSize: Float = WORLD_SIZE.x

        for (i: Int in 0 until CELL_HOLDERS) {
            cellHolders[i].getPosition().set(0f, (i * worldCellHolderSize))

            cellHolders[i].clear()
        }

        cellHolders[0].addCell(Cell.Type.EMPTY, 0)
        cellHolders[0].addCell(Cell.Type.EMPTY, 1)
        cellHolders[0].addCell(Cell.Type.EMPTY, 2)
        cellHolders[0].addCell(Cell.Type.EMPTY, 3)

        cellHolders[0].setOutputCell(Random.nextInt(2, 4))

        generateWorldCellHolder(1)
        generateWorldCellHolder(2)
    }

    /**
     * Generates randomly the content of the world's cell holder identified by the passed [cellHolderIndex].
     *
     * @param cellHolderIndex index of the world's cell holder whose content will be generated.
     *
     * @throws IllegalArgumentException if [cellHolderIndex] is out of limits.
     * @throws IllegalStateException if [cellGenerator] has not been initialized yet or if the output position of a cell
     * is not 3 or 4.
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun generateWorldCellHolder(cellHolderIndex: Int) {
        require(cellHolderIndex in 0 until CELL_HOLDERS) {"'cellHolderIndex' is out of limits."}
        check(this::cellGenerator.isInitialized) {"'cellGenerator' has not been initialized yet."}

        val inputCellHolder: CellHolder = if (cellHolderIndex > 0) cellHolders[cellHolderIndex - 1]
            else cellHolders[CELL_HOLDERS - 1]

        val inputPosition: Int = inputCellHolder.getOutputCell() - (CellHolder.HELD_CELLS / 2)
        val outputPosition: Int = Random.nextInt(2, 4)

        check(inputPosition in 0..1){"'inputPosition' is not a bottom cell."}

        cellHolders[cellHolderIndex].setOutputCell(outputPosition)
        cellGenerator.generateRoute(cellHolders[cellHolderIndex], true, CellHolder.WORLD_CELL_HOLDER_LEVEL,
                inputPosition, outputPosition, difficulty)
    }

    /**
     * Creates the world's cell holders and initializes the finger.
     *
     * @param dependencyManager instance of the [DependencyManager] used for this game's instance.
     * @param fingerRadius radius of the finger in pixels.
     */
    @Throws(IllegalStateException::class)
    fun create(dependencyManager: DependencyManager, fingerRadius: Float) {
        val assets: HashMap<String, Any> = dependencyManager.retrieveAssets("WORLD")

        check (assets.containsKey("WorldAtlas")) {"World dependency 'CellsAtlas' is not solved."}

        worldAtlas = assets["WorldAtlas"] as TextureAtlas

        worldBackground = assets["WorldBackground"] as Texture
        worldBackground.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

        val worldCellHolderSize: Float = WORLD_SIZE.x

        for (i: Int in 0 until CELL_HOLDERS) {
            val cellHolder: CellHolder = CellHolder(worldCellHolderSize, worldAtlas, CellHolder.WORLD_CELL_HOLDER_LEVEL)

            cellHolders.add(cellHolder)
        }

        initializeFinger(worldAtlas, fingerRadius)
        initializeWorldCellHolders()
    }

    /**
     * Draws the world's actors, meanwhile updating the score and the difficulty as time passes.
     *
     * @param batch batch used for drawing the world's actors.
     */
    fun render(batch: Batch) {
        score += Gdx.graphics.deltaTime

        if (score < TIME_UNTIL_MAX_DIFFICULTY) {
            difficulty = score / TIME_UNTIL_MAX_DIFFICULTY
            speed = MIN_SPEED + difficulty * (MAX_SPEED - MIN_SPEED)
        } else if (speed != 1f) {
            speed = MAX_SPEED
        }

        val distance: Float = (speed * score) * UNIT_TO_PIXELS
        val location: Int = if (distance >= Int.MAX_VALUE) (distance - floor(distance / Int.MAX_VALUE)).toInt()
        else distance.toInt()

        batch.draw(worldBackground, 0f, 0f, WORLD_SIZE.x, WORLD_SIZE.y, 0, location,
                WORLD_BACKGROUND_SIZE, WORLD_BACKGROUND_SIZE, false, false)

        var count: Int = 0
        for (cellHolderIndex: Int in 0 until cellHolders.size) {
            val cellHolder: CellHolder = cellHolders[cellHolderIndex]

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

        finger.draw(batch)
    }

    /**
     * Checks whether or not the [finger] is colliding with an inner cell of the passed [cellHolder].
     *
     * @param cellHolder cell holder whose inner cells will be checked for collision.
     * @param finger instance of finger to be checked for collision.
     *
     * @return whether or not the [finger] is colliding with [cellHolder]'s inner cells.
     */
    private fun isCollidingFingerWithCellHoldersInnerObstacles(cellHolder: CellHolder, finger: Finger): Boolean {
        for (cellIndex: Int in 0 until CellHolder.HELD_CELLS) {
            val cell: Cell = cellHolder.getCell(cellIndex)

            if (cell.getType() == Cell.Type.HOLDER) {
                if (isCollidingFingerWithCellHoldersInnerObstacles(cell as CellHolder, finger)) {
                    return true
                }
            } else if (cell is Obstacle) {
                if (cell.isFingerCollidingWithObstacle(finger)) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Displaces a world cell holder accordingly to the current [speed].
     *
     * @param cellHolder instance of world's cell holder to be displaced.
     */
    private fun displaceCellHolder(cellHolder: CellHolder) {
        cellHolder.setPosition(cellHolder.getPosition().sub(0f, speed))

        if (cellHolder.getPosition().y <= ( -1 * WORLD_SIZE.x)) { // if it's under the screen
            generateWorldCellHolder(cellHolders.indexOf(cellHolder))

            cellHolder.setPosition(cellHolder.getPosition().x,  WORLD_SIZE.x * 2f)
        }
    }

    /**
     * @param movementStartPoint point where the movement started.
     * @param movementEndPoint point where the movement ended.
     *
     * @return whether or not the movement described by [movementStartPoint] and [movementEndPoint] was fast enough
     * to avoid collision detection.
     */
    fun checkFastMovement(movementStartPoint: Vector2, movementEndPoint: Vector2) {
        val hypotheticalFinger: Finger = Finger(worldAtlas, this, worldViewport, finger.getRadius())
        val movementLength: Float = movementStartPoint.dst(movementEndPoint)
        val lineStep: Float = (movementEndPoint.y - movementStartPoint.y) / (movementEndPoint.x - movementStartPoint.x)

        for (step: Int in 0..ceil(movementLength / finger.getRadius()).toInt()) {
            val stepX: Float = movementStartPoint.x + (finger.getRadius() * step)
            val stepY: Float = lineStep * (stepX - movementStartPoint.x) + movementStartPoint.y

            hypotheticalFinger.updatePosition(stepX, stepY)

            for (cellHolder: CellHolder in cellHolders) {
                if (cellHolder.isFingerWithinCell(hypotheticalFinger)) {
                    if (isCollidingFingerWithCellHoldersInnerObstacles(cellHolder, finger)) {
                        gameOver()

                        return
                    }
                }
            }
        }
    }

    /**
     * Starts listening to the finger.
     */
    fun show() {
        fingerListener = FingerListener(finger, this)

        stage.addListener(fingerListener)
    }

    /**
     * Starts the world's generation and displacement.
     */
    fun start() {
        started = true
    }

    /**
     * @return whether or not the world is being generated and displaced.
     */
    fun isStarted(): Boolean {
        return started
    }

    /**
     * Stops the world's generation and displacement.
     */
    fun gameOver() {
        stage.removeListener(fingerListener)
        finger.setCollided(true)

        started = false
    }

    /**
     * Restarts the world's generation and displacement.
     */
    fun restart() {
        initializeWorldCellHolders()
        finger.updatePosition(0f, 0f)

        stage.addListener(fingerListener)
    }

    fun dispose() {

    }

    companion object {
        const val CELL_HOLDERS: Int = 3
        const val UNIT_TO_PIXELS: Int = 100

        const val WORLD_BACKGROUND_SIZE: Int = 2048

        const val FINGER_RADIUS_MARGIN: Float = 1.5f // lower = harder *evil laugh*, but never lower than 1.
        const val MAX_SPEED: Float = 0.05f
        const val MIN_SPEED: Float = 0.01f
        const val TIME_UNTIL_MAX_DIFFICULTY: Float = 15f // seconds

        val WORLD_SIZE: Vector2 = Vector2(8f, 16f)

        // Powered to the square in order to increase fast movement detection's efficiency.
        val FAST_MOVEMENT_DISTANCE_SQUARED: Float = WORLD_SIZE.x.pow(2)
    }
}
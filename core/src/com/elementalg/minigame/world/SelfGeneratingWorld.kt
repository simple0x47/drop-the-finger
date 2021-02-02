package com.elementalg.minigame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.elementalg.client.managers.DependencyManager
import com.elementalg.minigame.Game
import com.elementalg.minigame.ILeaderboard
import com.elementalg.minigame.world.cells.Cell
import com.elementalg.minigame.world.cells.CellContinuousGenerator
import com.elementalg.minigame.world.cells.CellHolder
import com.elementalg.minigame.world.cells.Obstacle
import kotlin.math.*
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
class SelfGeneratingWorld(
    private val stage: Stage, private val worldViewport: Viewport,
    private val gameOverListener: BasicListener
) {
    private val cellHolders: ArrayList<CellHolder> = ArrayList(CELL_HOLDERS)

    private val screenBorders: ArrayList<Vector2> = ArrayList()

    private var speed: Float = MIN_SPEED
    private var difficulty: Float = 0f
    private var started: Boolean = false
    private var score: Float = 0f
    private var backgroundDisplacement: Int = 0

    private var afterAd: Boolean = false
    private var timeAfterAd: Float = 0f

    private lateinit var worldAtlas: TextureAtlas
    private lateinit var screenDarker: TextureRegion

    private lateinit var worldBackground: Texture

    private lateinit var finger: Finger
    private lateinit var fingerListener: FingerListener
    private lateinit var cellGenerator: CellContinuousGenerator
    private lateinit var theme: Music
    private lateinit var gameOverSound: Music

    init {
        screenBorders.add(Vector2(0f, WORLD_SIZE.y))
        screenBorders.add(Vector2(WORLD_SIZE.x, WORLD_SIZE.y))
        screenBorders.add(Vector2(WORLD_SIZE.x, 0f))
        screenBorders.add(Vector2(0f, 0f))
    }

    fun getScore(): Float {
        return score
    }

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
        check(!this::finger.isInitialized) { "'finger' has already been initialized once." }

        finger = Finger(worldAtlas, this, worldViewport, fingerRadius)
        finger.updatePosition(WORLD_SIZE.x / 2f, WORLD_SIZE.x / 2f)
        fingerListener = FingerListener(finger, this, false)
        cellGenerator = CellContinuousGenerator(finger.getRadius())
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

        cellHolders[0].fill()

        cellHolders[0].outputCellPosition = Random.nextInt(2, CellHolder.HELD_CELLS)

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
        require(cellHolderIndex in 0 until CELL_HOLDERS) { "'cellHolderIndex' is out of limits." }
        check(this::cellGenerator.isInitialized) { "'cellGenerator' has not been initialized yet." }

        val inputCellHolder: CellHolder = if (cellHolderIndex > 0) cellHolders[cellHolderIndex - 1]
        else cellHolders[CELL_HOLDERS - 1]

        val inputPosition: Int = abs(inputCellHolder.outputCellPosition - 3)
        val outputPosition: Int = if (Random.nextBoolean()) 2 else 3

        check(inputPosition in 0..1) { "'inputPosition' is not a bottom cell." }

        cellHolders[cellHolderIndex].outputCellPosition = outputPosition

        cellGenerator.generateRoute(cellHolders[cellHolderIndex], inputCellHolder, difficulty)
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

        check(assets.containsKey("WorldAtlas")) { "World dependency 'CellsAtlas' is not solved." }

        worldAtlas = assets["WorldAtlas"] as TextureAtlas

        worldBackground = assets["WorldBackground"] as Texture
        worldBackground.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

        val worldCellHolderSize: Float = WORLD_SIZE.x

        for (i: Int in 0 until CELL_HOLDERS) {
            val cellHolder: CellHolder = CellHolder(null, worldCellHolderSize, worldAtlas)

            cellHolders.add(cellHolder)
        }

        initializeFinger(worldAtlas, fingerRadius)
        initializeWorldCellHolders()

        screenDarker = worldAtlas.findRegion("Background")

        theme = assets["WorldTheme"] as Music
        gameOverSound = assets["Hit"] as Music
    }

    /**
     * Draws the world's actors, meanwhile updating the score and the difficulty as time passes.
     *
     * @param batch batch used for drawing the world's actors.
     */
    fun render(batch: Batch) {
        val deltaTime: Float = Gdx.graphics.deltaTime
        timeAfterAd += deltaTime

        if (started) {
            score += deltaTime

            when {
                (score <= TIME_UNTIL_MAX_DIFFICULTY) -> {
                    difficulty = score / TIME_UNTIL_MAX_DIFFICULTY
                    speed = MIN_SPEED + difficulty * (MAX_SPEED - MIN_SPEED)
                }
                (speed != 1f) -> {
                    speed = MAX_SPEED
                }
                (difficulty != 1f) -> {
                    difficulty = 1f
                }
            }
        } else {
            if (theme.isPlaying) {
                theme.stop()
            }
        }

        val deltaSpeed: Float = speed * FPS_60
        backgroundDisplacement = (backgroundDisplacement + ceil(deltaSpeed).toInt()) % WORLD_BACKGROUND_SIZE

        batch.draw(
            worldBackground, 0f, 0f, WORLD_SIZE.x, WORLD_SIZE.y, 0, backgroundDisplacement,
            WORLD_BACKGROUND_SIZE, WORLD_BACKGROUND_SIZE, false, false
        )

        if (started && isCollidingFingerWithScreenBorders(finger)) {
            gameOver()
        }

        var cellHolderCount: Int = 0
        for (cellHolderIndex: Int in 0 until cellHolders.size) {
            val cellHolder: CellHolder = cellHolders[cellHolderIndex]

            cellHolder.draw(batch)

            if (started) {
                displaceCellHolder(cellHolder, deltaSpeed)

                if (cellHolderCount < 2) {
                    if (cellHolder.isFingerWithinCell(finger)) {
                        if (isCollidingFingerWithCellHoldersInnerObstacles(cellHolder, finger)) {
                            gameOver()
                        }

                        cellHolderCount++
                    }
                }
            }
        }

        finger.draw(batch)

        if (finger.hasCollided()) {
            batch.draw(screenDarker, 0f, 0f, 100f, 100f)
        }
    }

    private fun isCollidingFingerWithScreenBorders(finger: Finger): Boolean {
        val fingerPosition: Vector2 = finger.getPosition()

        for (line: Int in 0..3) {
            val firstPoint: Vector2 = screenBorders[line]
            val secondPoint: Vector2 = screenBorders[if ((line + 1) > 3) 0 else (line + 1)]

            val distance: Float = abs(
                (secondPoint.y - firstPoint.y) * fingerPosition.x -
                        (secondPoint.x - firstPoint.x) * fingerPosition.y +
                        secondPoint.x * firstPoint.y - secondPoint.y * firstPoint.x
            ) /
                    sqrt((secondPoint.y - firstPoint.y).pow(2) + (secondPoint.x - firstPoint.x).pow(2))

            if (distance <= finger.getRadius()) {
                return true
            }
        }

        return false
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
     * @param deltaSpeed speed of the frame.
     */
    private fun displaceCellHolder(cellHolder: CellHolder, deltaSpeed: Float) {
        cellHolder.setPosition(cellHolder.getPosition().sub(0f, deltaSpeed))

        if (cellHolder.getPosition().y <= (-1 * WORLD_SIZE.x)) { // if it's under the screen
            generateWorldCellHolder(cellHolders.indexOf(cellHolder))

            cellHolder.setPosition(cellHolder.getPosition().x, WORLD_SIZE.y)
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
        restart(false)
    }

    /**
     * Starts the world's generation and displacement.
     */
    fun start() {
        started = true

        speed = MIN_SPEED
        difficulty = 0f
        score = 0f

        theme.position = 0f
        theme.volume = WORLD_THEME_VOLUME
        theme.isLooping = true
        theme.play()
    }

    fun pause() {
        theme.stop()

        if (isStarted()) {
            gameOver()
        }
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

        theme.stop()

        val leaderboard: ILeaderboard = Game.instance().getLeaderboard()

        if (leaderboard.getHighScore() >= score) {
            gameOverSound.stop()
            gameOverSound.volume = 0.2f
            gameOverSound.setPan(1f, 0.2f)
            gameOverSound.play()
        }

        started = false
        difficulty = 0f

        Gdx.input.inputProcessor = null
        gameOverListener.handle()
        leaderboard.addScore(score)
    }

    fun stopGameOverSound() {
        gameOverSound.stop()
    }

    fun regenerateWorld() {
        initializeWorldCellHolders()
        finger.updatePosition(WORLD_SIZE.x / 2f, WORLD_SIZE.x / 2f)
    }

    /**
     * Restarts the world's generation and displacement.
     */
    fun restart(afterAd: Boolean) {
        started = false

        finger.restart()

        this.afterAd = afterAd
        timeAfterAd = 0f
        speed = MIN_SPEED
        difficulty = 0f
        score = 0f

        fingerListener = FingerListener(finger, this, afterAd)
        stage.addListener(fingerListener)
    }

    fun getTimerAfterAd(): Float {
        return timeAfterAd
    }

    fun hide() {

    }

    fun dispose() {
        theme.dispose()
        gameOverSound.dispose()
    }

    companion object {
        const val CELL_HOLDERS: Int = 3

        const val WORLD_BACKGROUND_SIZE: Int = 2048
        const val WORLD_THEME_VOLUME: Float = 0.2f

        const val MAX_SPEED: Float = 3.3f
        const val MIN_SPEED: Float = 1.5f
        const val TIME_UNTIL_MAX_DIFFICULTY: Float = 37f // seconds
        const val WAIT_TIME_AFTER_AD: Float = 0.3f

        const val FPS_60: Float = 1/60f
        val WORLD_SIZE: Vector2 = Vector2(8f, 16f)


        // Powered to the square in order to increase fast movement detection's efficiency.
        val FAST_MOVEMENT_DISTANCE_SQUARED: Float = WORLD_SIZE.x.pow(2)
    }
}
package com.elementalg.minigame.world

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

/**
 * Handles the usage of the finger in the game.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an instance with the passed parameters.
 * @param finger instance of finger which will be updated as input events are listened.
 * @param world instance of world in which the finger is located.
 */
class FingerListener(private val finger: Finger, private val world: World) : ClickListener() {
    private val lastPosition: Vector2 = Vector2()

    /**
     * Proceeds to start the world, and this way, the game.
     */
    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        world.start()

        lastPosition.set(x, y)
        return super.touchDown(event, x, y, pointer, button)
    }

    /**
     * If the world is started, it calls game over.
     */
    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        if (world.isStarted()) {
            world.gameOver()
        }

        super.touchUp(event, x, y, pointer, button)
    }

    /**
     * Detects if the player moved the finger fast in order to try to avoid obstacle's collision calculations,
     * and proceeds to perform a calculation of collision.
     * It also updates the position of the finger if the world is started.
     */
    override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
        if (lastPosition.dst2(x, y) > World.FAST_MOVEMENT_DISTANCE_SQUARED) {
            world.checkFastMovement(lastPosition, Vector2(x, y))
        }

        if (world.isStarted()) {
            finger.updatePosition(x, y)
        }

        super.touchDragged(event, x, y, pointer)
    }
}
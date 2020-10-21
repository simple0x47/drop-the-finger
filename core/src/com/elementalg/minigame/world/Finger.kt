package com.elementalg.minigame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport

/**
 * Represents the usage of the finger in the game.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an instance with the passed parameters.
 * @param worldAtlas texture atlas which contains the world's actors.
 * @param selfGeneratingWorld world where the finger will be located in.
 * @param worldViewport viewport used for the world's actors.
 * @param radius radius of the finger in world's units.
 */
class Finger(worldAtlas: TextureAtlas, private val selfGeneratingWorld: SelfGeneratingWorld, private val worldViewport: Viewport,
             private val radius: Float) {
    private val fingerPointer: TextureRegion
    private val collisionAnimation: Animation<TextureRegion>

    private val position: Vector2 = Vector2()
    private var collided: Boolean = false
    private var collisionTime: Float = 0f

    init {
        fingerPointer = worldAtlas.findRegion(POINTER_REGION)
        collisionAnimation = Animation(COLLISION_ANIMATION_FRAME_DURATION,
                worldAtlas.findRegions(COLLISION_ANIMATION_BASE_KEY), Animation.PlayMode.NORMAL)
    }

    fun getRadius(): Float {
        return radius
    }

    fun getPosition(): Vector2 {
        return position
    }

    /**
     * Updates the position of the finger accordingly to the input coordinates.
     */
    fun updatePosition(inputX: Float, inputY: Float) {
        position.set(inputX, inputY)
    }

    /**
     * @return whether or not the finger has collided with an obstacle.
     */
    fun hasCollided(): Boolean {
        return collided
    }

    /**
     * @param collided whether or not the finger has collided with an obstacle.
     */
    fun setCollided(collided: Boolean) {
        this.collided = collided
    }

    fun draw(batch: Batch) {
        if (collided && (collisionTime < COLLISION_ANIMATION_DURATION)) {
            collisionTime += Gdx.graphics.deltaTime

            batch.draw(collisionAnimation.getKeyFrame(collisionTime), getPosition().x - radius,
                    getPosition().y - radius, radius * 2f, radius * 2f)
        } else if (!collided) {
            batch.draw(fingerPointer, getPosition().x - radius,
                    getPosition().y - radius, radius * 2f, radius * 2f)
        }
    }

    fun restart() {
        collided = false
        collisionTime = 0f
    }

    companion object {
        const val FINGER_INCH_RADIUS: Float = 0.15f

        const val POINTER_REGION: String = "Finger"
        const val COLLISION_ANIMATION_BASE_KEY: String = "collision"
        const val COLLISION_ANIMATION_FRAME_DURATION: Float = 0.02f
        const val COLLISION_ANIMATION_DURATION: Float = 0.58f
    }
}
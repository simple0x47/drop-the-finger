package com.elementalg.minigame.world.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.world.Finger

/**
 * Square shaped obstacle.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an instance with the passed parameters.
 * @param size cell's side size.
 * @param textureRegion region of the texture where the square's texture data is located.
 */
class SquareObstacle(parentCell: CellHolder?, size: Float, private val textureRegion: TextureRegion) :
        Obstacle(parentCell, Type.SQUARE, size) {
    private val origin: Float = getSize() / 2f

    override fun setPosition(position: Vector2) {
        getPosition().set(position)
    }

    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y)
    }

    /**
     * @param finger instance of finger to be checked.
     *
     * @return whether or not the [finger] is colliding with the square.
     */
    override fun isFingerCollidingWithObstacle(finger: Finger): Boolean {
        return isFingerWithinCell(finger)
    }

    override fun draw(batch: Batch) {
        batch.draw(textureRegion, getPosition().x, getPosition().y, origin, origin, getSize(), getSize(), 1f,
                1f, 0f)
    }

    companion object {
        const val TEXTURE_REGION: String = "Square"
    }
}
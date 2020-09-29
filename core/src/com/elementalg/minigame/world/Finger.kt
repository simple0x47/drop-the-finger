package com.elementalg.minigame.world

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport

class Finger(private val world: World, private val worldViewport: Viewport, private val textureRegion: TextureRegion,
             private val radius: Float) {
    private val position: Vector2 = Vector2()

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

    fun draw(batch: Batch) {
        batch.draw(textureRegion, getPosition().x - radius, getPosition().y - radius, radius * 2f, radius * 2f)
    }

    companion object {
        const val FINGER_INCH_RADIUS: Float = 0.15f

        const val TEXTURE_REGION: String = "Finger"
    }
}
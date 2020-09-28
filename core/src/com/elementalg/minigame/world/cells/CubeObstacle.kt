package com.elementalg.minigame.world.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.world.Finger

class CubeObstacle(size: Float, private val textureRegion: TextureRegion) : Obstacle(Type.CUBE, size) {
    private val origin: Float = getSize() / 2f

    override fun setPosition(position: Vector2) {
        getPosition().set(position)
    }

    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y)
    }

    override fun isFingerCollidingWithObstacle(finger: Finger): Boolean {
        return isFingerWithinCell(finger)
    }

    override fun draw(batch: Batch) {
        batch.draw(textureRegion, getPosition().x, getPosition().y, origin, origin, getSize(), getSize(), 1f,
                1f, 0f)
    }

    companion object {
        const val TEXTURE_REGION: String = "Cube"
    }
}
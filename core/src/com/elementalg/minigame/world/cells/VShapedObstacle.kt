package com.elementalg.minigame.world.cells

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.elementalg.minigame.world.Finger

/**
 * V shaped obstacle.
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes an instance with the passed parameters.
 * @param size cell's side size.
 * @param textureRegion region of the texture where the v's texture data is located.
 */
class VShapedObstacle(parentCell: CellHolder?, size: Float, private val textureRegion: TextureRegion) :
        Obstacle(parentCell, Type.V, size) {
    private val origin: Float = size / 2f

    override fun isFingerCollidingWithObstacle(finger: Finger): Boolean {
        if (isFingerWithinCell(finger)) {
            val fingerPosition: Vector2 = finger.getPosition()

            for (i: Int in 0 until vertexArrayList.size) {
                val segmentPoint1: Vector2 = (Vector2(vertexArrayList[i]).scl(getSize())).add(getPosition())
                val segmentPoint2: Vector2 = (Vector2(vertexArrayList[if (i + 1 >= vertexArrayList.size) 0 else i + 1])
                        .scl(getSize())).add(getPosition())

                val dist: Float = distanceBetweenSegmentAndPoint(segmentPoint1, segmentPoint2, fingerPosition)

                if (dist <= finger.getRadius()) {
                    return true
                }
            }
        }

        return false
    }

    override fun setPosition(position: Vector2) {
        getPosition().set(position)
    }

    override fun setPosition(x: Float, y: Float) {
        getPosition().set(x, y)
    }

    override fun draw(batch: Batch) {
        batch.draw(textureRegion, getPosition().x, getPosition().y, origin, origin, getSize(), getSize(), 1f,
                1f, 0f)
    }

    companion object {
        const val TEXTURE_REGION: String = "V"
        private val vertexArrayList: ArrayList<Vector2> = ArrayList()

        init {
            vertexArrayList.add(Vector2(0.5f, 0f))
            vertexArrayList.add(Vector2(0f, 1f))
            vertexArrayList.add(Vector2(0.07125f, 1f))
            vertexArrayList.add(Vector2(0.5f, 0.14375f))
            vertexArrayList.add(Vector2(0.9275f, 1f))
            vertexArrayList.add(Vector2(1f, 1f))
        }
    }
}
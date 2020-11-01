package com.elementalg.minigame.screens

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

class ScoreFirework(private val position: Vector2, private val size: Float,
                    private val animation: Animation<TextureRegion>) {

    private var elapsedTime: Float = 0f

    fun draw(batch: Batch, delta: Float): Boolean {
        batch.draw(animation.getKeyFrame(elapsedTime), position.x, position.y, size, size)
        elapsedTime += delta

        return animation.isAnimationFinished(elapsedTime)
    }
}
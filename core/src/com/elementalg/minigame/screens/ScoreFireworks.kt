package com.elementalg.minigame.screens

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class ScoreFireworks(private val position: Vector2, private val size: Vector2,
                     private val totalFireworks: Int, scoreAtlas: TextureAtlas) {
    private val scoreFireworkAnimation02: Animation<TextureRegion>
    private val scoreFireworkAnimation03: Animation<TextureRegion>

    private val fireworks: ArrayList<ScoreFirework> = ArrayList()

    private var beingShown: Boolean = false
    private var fireworksCount: Int = 0

    private var latestFireworkType: Int = 0

    init {
        scoreFireworkAnimation02 = Animation(ANIMATION_FRAME_DURATION,
                scoreAtlas.findRegions(ANIMATION_02_BASE_KEY))
        scoreFireworkAnimation03 = Animation(ANIMATION_FRAME_DURATION,
                scoreAtlas.findRegions(ANIMATION_03_BASE_KEY))
    }

    private fun generateFirework() {
        val nextAnimationId: Int =  if (latestFireworkType == 0) {
            Random.nextInt(2, 4)
        } else {
            if (latestFireworkType == 2) 3 else 2
        }

        val nextAnimation: Animation<TextureRegion> = if (nextAnimationId == 2)
            scoreFireworkAnimation02 else scoreFireworkAnimation03

        val nextAnimationSize: Float = max(ANIMATION_MINIMUM_SIZE,
               min(size.x, size.y) * Random.nextFloat())

        val nextAnimationPosition: Vector2 = Vector2()

        nextAnimationPosition.x = position.x + (Random.nextFloat() * (size.x - nextAnimationSize))
        nextAnimationPosition.y = position.y + (Random.nextFloat() * (size.y - nextAnimationSize))

        fireworks.add(ScoreFirework(nextAnimationPosition, nextAnimationSize, nextAnimation))

        fireworksCount++

        latestFireworkType = nextAnimationId
    }

    fun show() {
        generateFirework()
        beingShown = true
    }

    fun draw(batch: Batch, delta: Float) {
        if (beingShown) {
            var fireworkToBeDeleted: ScoreFirework? = null

            for (firework: ScoreFirework in fireworks) {
                if (firework.draw(batch, delta)) {
                    fireworkToBeDeleted = firework
                }
            }

            if (fireworkToBeDeleted != null) {
                fireworks.remove(fireworkToBeDeleted)

                if (fireworksCount < totalFireworks) {
                    generateFirework()
                }
            }

            if ((fireworksCount < totalFireworks) && (fireworks.size < LIMIT_OF_FIREWORKS_AT_SAME_TIME)) {
                if (Random.nextFloat() < SIMULTANEOUS_FIREWORK_CHANCE) {
                    generateFirework()
                }
            }
        }
    }

    fun hide() {
        fireworksCount = 0
        beingShown = false
    }

    companion object {
        private const val ANIMATION_02_BASE_KEY= "Fireworks02"
        private const val ANIMATION_03_BASE_KEY = "Fireworks03"
        private const val ANIMATION_FRAME_DURATION = 0.02f

        private const val ANIMATION_MINIMUM_SIZE = 64f
        private const val SIMULTANEOUS_FIREWORK_CHANCE = 0.2f
        private const val LIMIT_OF_FIREWORKS_AT_SAME_TIME = 3
    }
}
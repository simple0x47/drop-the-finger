package com.elementalg.minigame.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.elementalg.minigame.Game
import kotlin.jvm.Throws

class ScoreWidget(indicatorFont: BitmapFont, valueFont: BitmapFont, color: Color) {
    private val scoreIndicator: Label
    private val scoreValue: Label

    init {
        val indicatorStyle: Label.LabelStyle = Label.LabelStyle()
        indicatorStyle.font = indicatorFont
        indicatorStyle.fontColor = color
        val indicatorText: String = Game.instance().getLocaleManager().get("SCORE_INDICATOR")
        scoreIndicator = Label(indicatorText, indicatorStyle)
        scoreIndicator.setSize(128f, 128f)
        scoreIndicator.setPosition(16f, 0f)
        scoreIndicator.setAlignment(Align.bottomLeft)

        val valueX: Float = scoreIndicator.x + (indicatorText.length * 24)
        val valueStyle: Label.LabelStyle = Label.LabelStyle()
        valueStyle.font = valueFont
        valueStyle.fontColor = color
        scoreValue = Label("0", valueStyle)
        scoreValue.setSize(128f, 128f)
        scoreValue.setPosition(valueX, 0f)
        scoreValue.setAlignment(Align.bottomLeft)
    }

    @Throws(IllegalArgumentException::class)
    fun updateScore(score: Float) {
        if (score < 0f) {
            throw IllegalArgumentException("'score' cannot be negative.")
        }

        val scoreText: String = String.format("%.1f", score)
        scoreValue.setPosition(scoreIndicator.x + (scoreIndicator.text.length * 18), scoreIndicator.y)
        scoreValue.setText(scoreText)
    }

    fun draw(batch: Batch) {
        scoreIndicator.draw(batch, 1f)
        scoreValue.draw(batch, 1f)
    }
}
package com.elementalg.minigame.screens

class ScoreMessageProvider {
    private val goodMessages: ScoreMessage = ScoreMessage(ScoreMessage.Type.GOOD)
    private val neutralMessages: ScoreMessage = ScoreMessage(ScoreMessage.Type.NEUTRAL)
    private val badMessages: ScoreMessage = ScoreMessage(ScoreMessage.Type.BAD)
    private val goatMessages: ScoreMessage = ScoreMessage(ScoreMessage.Type.GOAT)

    fun retrieveMessage(type: ScoreMessage.Type): String {
        return when (type) {
            ScoreMessage.Type.GOOD -> goodMessages.getMessage()
            ScoreMessage.Type.NEUTRAL -> neutralMessages.getMessage()
            ScoreMessage.Type.BAD -> badMessages.getMessage()
            ScoreMessage.Type.GOAT -> goatMessages.getMessage()
        }
    }
}
package com.elementalg.minigame.screens

class ScoreMessageProvider {
    private val goodMessages: ScoreMessage = ScoreMessage(ScoreMessage.Type.GOOD)
    private val neutralMessages: ScoreMessage = ScoreMessage(ScoreMessage.Type.NEUTRAL)
    private val badMessages: ScoreMessage = ScoreMessage(ScoreMessage.Type.BAD)

    fun retrieveMessage(type: ScoreMessage.Type): String {
        return when (type) {
            ScoreMessage.Type.GOOD -> goodMessages.getMessage()
            ScoreMessage.Type.NEUTRAL -> neutralMessages.getMessage()
            ScoreMessage.Type.BAD -> badMessages.getMessage()
        }
    }
}
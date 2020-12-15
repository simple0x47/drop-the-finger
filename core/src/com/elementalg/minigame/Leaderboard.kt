package com.elementalg.minigame

interface Leaderboard {
    fun addScore(score: Float)
    fun getHighScore(): Float
    fun getWorldHighScore(): Float

    fun showLeaderboard()
}
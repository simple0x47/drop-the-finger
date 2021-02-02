package com.elementalg.minigame

interface ILeaderboard {
    fun addScore(score: Float)
    fun getHighScore(): Float
    fun getWorldHighScore(): Float

    fun showLeaderboard()
}
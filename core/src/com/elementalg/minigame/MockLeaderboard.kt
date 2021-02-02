package com.elementalg.minigame

/**
 * Mock leaderboard used for debug versions.
 *
 * @author Gabriel Amihalachioaie.
 */
class MockLeaderboard : ILeaderboard {
    private var highScore: Float = 50f

    override fun addScore(score: Float) {
        highScore = score
    }

    override fun getHighScore(): Float {
        return highScore
    }

    override fun getWorldHighScore(): Float {
        return highScore
    }

    override fun showLeaderboard() {

    }
}
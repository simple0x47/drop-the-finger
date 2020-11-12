package com.elementalg.minigame

class MockLeaderboard : Leaderboard {
    override fun addScore(score: Float) {

    }

    override fun getHighScore(): Float {
        return 5f
    }

    override fun getWorldHighScore(): Float {
        return 10f
    }

    override fun showLeaderboard() {

    }
}
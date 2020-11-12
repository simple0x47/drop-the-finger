package com.elementalg.minigame

import android.widget.Toast
import com.google.android.gms.games.LeaderboardsClient
import com.google.android.gms.games.leaderboard.LeaderboardVariant

class GoogleLeaderboard(private val launcher: AndroidLauncher,
                        private val leaderboardsClient: LeaderboardsClient) : Leaderboard {
    private var currentHighScore: Float = 0f
    private var worldHighScore: Float = 0f

    init {
        leaderboardsClient.loadCurrentPlayerLeaderboardScore(SCORE_ID, LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC).addOnCompleteListener {
            if (it.isSuccessful) {
                if ((it.result != null) && (it.result!!.get() != null)) {
                    currentHighScore = it.result!!.get()!!.rawScore / 10f
                }
            }
        }

        leaderboardsClient.loadTopScores(SCORE_ID, LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC, 1).addOnCompleteListener {
            if (it.isSuccessful) {
                if ((it.result != null) && (it.result!!.get() != null)) {
                    worldHighScore = it.result!!.get()!!.scores.get(0).rawScore / 10f
                    Toast.makeText(launcher.context, "WORLD RECORD$worldHighScore", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun addScore(score: Float) {
        if (score > 0f) {
            val newScore: Long = (score * 10f).toLong()

            leaderboardsClient.submitScore(SCORE_ID, newScore)

            if (score > currentHighScore) {
                currentHighScore = score
            }
        }
    }

    override fun getHighScore(): Float {
        return currentHighScore
    }

    override fun getWorldHighScore(): Float {
        return worldHighScore
    }

    override fun showLeaderboard() {
        leaderboardsClient.getLeaderboardIntent(SCORE_ID, LeaderboardVariant.TIME_SPAN_ALL_TIME).addOnCompleteListener {
            if (it.isSuccessful && (it.result != null)) {
                launcher.startActivityForResult(it.result!!, RC_SHOW_LEADERBOARD)
            }
        }

    }

    companion object {
        private const val SCORE_ID: String = "CgkIrfPH0dcSEAIQAg"
        const val RC_SHOW_LEADERBOARD: Int = 0x1011
    }
}
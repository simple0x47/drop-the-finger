/*
Code not removed, because it may provide to be useful for those looking for ways of implementing
Google's Leaderboard into a LibGDX Game.

package com.elementalg.minigame

import com.google.android.gms.games.LeaderboardsClient
import com.google.android.gms.games.leaderboard.LeaderboardVariant

/**
 * Implementation of Google's Game Service Leaderboard.
 *
 * @author Gabriel Amihalachioaie.
 */
class GoogleLeaderboard(
    private val launcher: AndroidLauncher,
    private val leaderboardsClient: LeaderboardsClient
) : ILeaderboard {

    init {
        leaderboardsClient.loadCurrentPlayerLeaderboardScore(
            SCORE_ID, LeaderboardVariant.TIME_SPAN_ALL_TIME,
            LeaderboardVariant.COLLECTION_PUBLIC
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                if ((it.result != null) && (it.result!!.get() != null)) {
                    currentHighScore = it.result!!.get()!!.rawScore / 10f
                }
            }
        }

        leaderboardsClient.loadTopScores(
            SCORE_ID, LeaderboardVariant.TIME_SPAN_ALL_TIME,
            LeaderboardVariant.COLLECTION_PUBLIC, 1
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                if ((it.result != null) && (it.result!!.get() != null)) {
                    worldHighScore = it.result!!.get()!!.scores.get(0).rawScore / 10f
                }
            }
        }
    }

    override fun addScore(score: Float) {
        if ((score > MINIMUM_SCORE_FOR_SEND) && (score < MAXIMUM_SCORE_FOR_SEND)) {
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
        private const val MINIMUM_SCORE_FOR_SEND: Float = 10f
        private const val MAXIMUM_SCORE_FOR_SEND: Float = 86400f
        private const val SCORE_ID: String = ""
        const val RC_SHOW_LEADERBOARD: Int = 0x1011

        private var currentHighScore: Float = 5f
        private var worldHighScore: Float = 347f
    }
}*/
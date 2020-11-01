package com.elementalg.minigame

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import com.badlogic.gdx.backends.android.AndroidApplication
import android.os.Bundle
import android.util.DisplayMetrics
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.games.Games
import com.google.android.gms.games.LeaderboardsClient
import java.util.*

class AndroidLauncher : AndroidApplication() {
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)

        val config = AndroidApplicationConfiguration()
        config.numSamples = 4

        game = Game(Locale.getDefault(), displayMetrics.xdpi, displayMetrics.ydpi)

        initialize(game, config)
    }

    override fun onResume() {
        super.onResume()

        signIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.failed_gs_title)
            builder.setMessage(R.string.failed_gs_message)
            builder.setPositiveButton(R.string.retry_sign_in) { dialog, _ ->
                signIn()
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.exit_game) { dialog, _ ->
                dialog.cancel()
                exit()
            }

            val result: GoogleSignInResult? = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result != null) {
                if (result.isSuccess && (result.signInAccount != null)) {
                    retrieveLeaderboard(result.signInAccount!!)
                }
                else {
                    builder.create()
                }
            } else {
                builder.create()
            }
        }
    }

    private fun signIn() {
        val client: GoogleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)

        client.silentSignIn().addOnCompleteListener {
            if (it.isSuccessful && (it.result != null)) {
                retrieveLeaderboard(it.result!!)
            }
            else {
                startActivityForResult(client.signInIntent, RC_SIGN_IN)
            }
        }
    }

    private fun retrieveLeaderboard(account: GoogleSignInAccount) {
        val leaderboardClient: LeaderboardsClient = Games.getLeaderboardsClient(context, account)

        val leaderboard: GoogleLeaderboard = GoogleLeaderboard(this, leaderboardClient)

        game.initializeLeaderboard(leaderboard)
    }

    companion object {
        private const val RC_SIGN_IN: Int = 0x1010
    }
}
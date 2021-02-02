/*
Code not removed because it may provide to be helpful for those looking for an implementation of Google's Leaderboard
and AdMob into a LibGDX game.

package com.elementalg.minigame


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import androidx.multidex.MultiDex
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.games.Games
import com.google.android.gms.games.LeaderboardsClient
import java.util.*

/**
 * Launcher used for release versions.
 *
 * @author Gabriel Amihalachioaie.
 */
class AndroidLauncher : AndroidApplication() {
    private lateinit var game: Game
    private lateinit var adMob: AdMobImplementation
    private lateinit var leaderboard: GoogleLeaderboard

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)

        val config = AndroidApplicationConfiguration()
        config.numSamples = 2
        config.useImmersiveMode = true

        Toast.makeText(context, R.string.loading, Toast.LENGTH_SHORT).show()

        adMob = AdMobImplementation(this)
        adMob.onCreate(context)
        adMob.load()

        val onScreenNotification: AndroidOnScreenNotification = AndroidOnScreenNotification(this)

        game = Game(
            Locale.getDefault(), displayMetrics.xdpi, displayMetrics.ydpi,
            adMob, onScreenNotification
        )

        initialize(game, config)
    }

    override fun onResume() {
        super.onResume()

        checkGoogleApiAvailability()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInResult? = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result != null) {
                if (result.isSuccess && (result.signInAccount != null)) {
                    retrieveLeaderboard(result.signInAccount!!)
                } else {
                    if (result.status.hasResolution()) {
                        Toast.makeText(context, R.string.retrying_sign_in, Toast.LENGTH_LONG).show()
                        signIn()
                    } else {
                        Toast.makeText(context, R.string.failed_sign_in, Toast.LENGTH_SHORT).show()
                        finishAffinity()
                    }
                }
            } else {
                val retryDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                retryDialogBuilder.setTitle(R.string.failed_gs_title)
                retryDialogBuilder.setMessage(R.string.failed_gs_message)
                retryDialogBuilder.setPositiveButton(R.string.retry_sign_in) { dialog, _ ->
                    signIn()
                    dialog.dismiss()
                }
                retryDialogBuilder.setNegativeButton(R.string.exit_game) { dialog, _ ->
                    dialog.cancel()
                    exit()
                }

                retryDialogBuilder.show()
            }
        } else if (requestCode == RC_GOOGLE_API_FIX) {
            Thread {
                Thread.sleep(GOOGLE_API_WAIT)
                checkGoogleApiAvailability()
            }.start()
        }
    }

    private fun checkGoogleApiAvailability() {
        val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()

        when (val availability: Int = apiAvailability.isGooglePlayServicesAvailable(context)) {
            ConnectionResult.SUCCESS -> {
                signIn()
            }
            ConnectionResult.SERVICE_UPDATING -> {
                Toast.makeText(context, R.string.updating_gps, Toast.LENGTH_LONG).show()

                Thread {
                    Thread.sleep(GOOGLE_API_UPDATING_WAIT)
                    checkGoogleApiAvailability()
                }.start()
            }
            ConnectionResult.SERVICE_MISSING -> {
                apiAvailability.getErrorDialog(this, availability, RC_GOOGLE_API_FIX)
            }
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> {
                apiAvailability.getErrorDialog(this, availability, RC_GOOGLE_API_FIX)
            }
            ConnectionResult.SERVICE_DISABLED -> {
                apiAvailability.getErrorDialog(this, availability, RC_GOOGLE_API_FIX)
            }
            ConnectionResult.SERVICE_INVALID -> {
                apiAvailability.getErrorDialog(this, availability, RC_GOOGLE_API_FIX)
            }
        }
    }

    private fun signIn() {
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)

        if (account == null) {
            val client: GoogleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)

            client.silentSignIn().addOnCompleteListener {
                if (it.isSuccessful && (it.result != null)) {
                    retrieveLeaderboard(it.result!!)
                } else {
                    Toast.makeText(context, R.string.signing_in, Toast.LENGTH_SHORT).show()
                    startActivityForResult(client.signInIntent, RC_SIGN_IN)
                }
            }
        } else {
            retrieveLeaderboard(account)
        }
    }

    private fun retrieveLeaderboard(account: GoogleSignInAccount) {
        val leaderboardClient: LeaderboardsClient = Games.getLeaderboardsClient(context, account)

        leaderboard = GoogleLeaderboard(this, leaderboardClient)

        game.initializeLeaderboard(leaderboard)
    }

    companion object {
        private const val RC_SIGN_IN: Int = 0x1010
        private const val RC_GOOGLE_API_FIX: Int = 0x1100

        private const val GOOGLE_API_WAIT: Long = 500L
        private const val GOOGLE_API_UPDATING_WAIT: Long = 2000L
    }
}
*/
package com.elementalg.minigame

import android.widget.Toast
import com.badlogic.gdx.backends.android.AndroidApplication

/**
 * Native on screen notification supplier for Android devices.
 *
 * @author Gabriel Amihalachioaie.
 */
class AndroidOnScreenNotification(private val launcher: AndroidApplication) :
    IOperatingSystemOnScreenNotification {
    override fun showNotification(text: String) {
        launcher.runOnUiThread {
            Toast.makeText(launcher.context, text, Toast.LENGTH_SHORT).show()
        }
    }
}
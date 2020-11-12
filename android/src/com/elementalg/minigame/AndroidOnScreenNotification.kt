package com.elementalg.minigame

import android.widget.Toast

class AndroidOnScreenNotification(private val launcher: AndroidLauncher) :
        IOperatingSystemOnScreenNotification {
    override fun showNotification(text: String) {
        launcher.runOnUiThread {
            Toast.makeText(launcher.context, text, Toast.LENGTH_SHORT).show()
        }
    }
}
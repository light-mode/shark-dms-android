package vn.sharkdms.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import vn.sharkdms.R

class TaskRemoveListener : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val name = getString(R.string.shared_prefs_private)
        val sharedPreferences = getSharedPreferences(name, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(getString(R.string.show_splash_screen_key), true)
        editor.apply()
        stopSelf()
    }
}
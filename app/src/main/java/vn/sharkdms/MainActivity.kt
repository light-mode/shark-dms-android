package vn.sharkdms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.service.TaskRemoveListener

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<SharedViewModel>()
    private val connectivityChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) {
                return
            }
            if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                viewModel.connectivity.postValue(false)
            } else {
                viewModel.connectivity.postValue(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, TaskRemoveListener::class.java))
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityChangeReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(connectivityChangeReceiver)
    }
}
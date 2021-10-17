package vn.sharkdms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.ui.customer.list.CustomerListFragment
import vn.sharkdms.ui.history.HistoryOrderListFragment
import vn.sharkdms.ui.manual.ManualFragment
import vn.sharkdms.ui.overview.OverviewFragment
import vn.sharkdms.ui.policy.PolicyFragment
import vn.sharkdms.ui.report.ReportFragment
import vn.sharkdms.ui.tasks.TasksFragment

@AndroidEntryPoint
class SaleActivity : AppCompatActivity() {

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
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale)
        viewModel.token = intent.getStringExtra("token").toString()
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.nav_header_text_view_username)
        usernameTextView.text = intent.getStringExtra("username").toString()
        val roleTextView = headerView.findViewById<TextView>(R.id.nav_header_text_view_role)
        roleTextView.text = intent.getStringExtra("role_name").toString()
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(navView, navController)
        navView.setCheckedItem(R.id.overviewFragment)
        headerView.setOnClickListener {
            Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.action_global_accountFragment)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        navView.menu.apply {
            findItem(R.id.action_call).setOnMenuItemClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + getString(R.string.support_call))
                startActivity(intent)
                true
            }
            findItem(R.id.action_email).setOnMenuItemClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:" + getString(R.string.support_email))
                startActivity(intent)
                true
            }
            findItem(R.id.action_logout).setOnMenuItemClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                Navigation.findNavController(this@SaleActivity, R.id.nav_host_fragment)
                    .navigate(R.id.action_global_logoutDialogFragment)
                true
            }
        }
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val currentFragment = supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment)?.childFragmentManager?.fragments?.get(0)
            if (currentFragment is OverviewFragment || currentFragment is CustomerListFragment ||
                currentFragment is HistoryOrderListFragment || currentFragment is ReportFragment
                || currentFragment is TasksFragment || currentFragment is PolicyFragment ||
                currentFragment is ManualFragment) finish()
            else super.onBackPressed()
        }
    }

    fun toggleNavigationDrawer(view: View) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
}
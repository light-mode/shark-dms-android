package vn.sharkdms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_customer.*
import kotlinx.coroutines.flow.collect
import vn.sharkdms.ui.cart.Cart
import vn.sharkdms.ui.historycustomer.list.CustomerHistoryOrderListFragment
import vn.sharkdms.util.Utils

@AndroidEntryPoint
class CustomerActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_customer)
        viewModel.token = intent.getStringExtra("token").toString()
        val activityToolbar = findViewById<Toolbar>(R.id.activity_toolbar)
        val customerIcon = findViewById<ImageView>(R.id.icon_customer)
        customerIcon.setOnClickListener {
            Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.action_global_accountFragment)
        }
        val usernameTextView = activityToolbar.findViewById<TextView>(
            R.id.toolbar_text_view_username)
        usernameTextView.text = intent.getStringExtra("username").toString()
        val roleTextView = activityToolbar.findViewById<TextView>(R.id.toolbar_text_view_role)
        roleTextView.text = intent.getStringExtra("role_name").toString()
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        bottom_nav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val destinationId = destination.id
            if (destinationId == R.id.productsFragment || destinationId == R.id
                    .customerHistoryOrderListFragment) {
                activityToolbar.visibility = View.VISIBLE
                bottom_nav.visibility = View.VISIBLE
            } else {
                activityToolbar.visibility = View.GONE
                bottom_nav.visibility = View.GONE
            }
        }
        activityToolbar.findViewById<ImageView>(R.id.icon_customer).setOnClickListener {
            Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.action_global_customerAccountFragment)
        }
        val cartIcon = activityToolbar.findViewById<ImageView>(R.id.icon_cart)
        cartIcon.setOnClickListener {
            if (bottom_nav.selectedItemId != R.id.productsFragment) {
                bottom_nav.selectedItemId = R.id.productsFragment
            }
            viewModel.getCartInfoAsCustomer()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.customerEvent.collect { event ->
                when (event) {
                    is SharedViewModel.CustomerEvent.NavigateToCartInfoScreen ->
                        handleGetCartInfoResponse(
                        event.cart)
                    is SharedViewModel.CustomerEvent.ShowNetworkConnectionErrorMessage ->
                        showNetworkConnectionErrorMessage()
                    is SharedViewModel.CustomerEvent.ShowUnauthorizedDialog -> {
                        Utils.showUnauthorizedDialog(this@CustomerActivity)
                    }
                }
            }
        }
    }

    private fun handleGetCartInfoResponse(cart: Cart?) {
        val bundle = Bundle()
        bundle.putParcelable("cart", cart)
        Navigation.findNavController(this, R.id.nav_host_fragment)
            .navigate(R.id.action_global_cartDetailsFragment2, bundle)
    }

    private fun showNetworkConnectionErrorMessage() {
        Toast.makeText(this, getString(R.string.message_connectivity_off), Toast.LENGTH_SHORT)
            .show()
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
        val currentFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment)?.childFragmentManager?.fragments?.get(0)
        if (currentFragment is CustomerHistoryOrderListFragment) finish()
        else super.onBackPressed()
    }
}
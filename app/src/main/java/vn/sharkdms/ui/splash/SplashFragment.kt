package vn.sharkdms.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.CustomerActivity
import vn.sharkdms.R
import vn.sharkdms.SaleActivity

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel by viewModels<SplashViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.splashEvent.collect { event ->
                when (event) {
                    is SplashViewModel.SplashEvent.ShowLoginScreen -> navigateToLoginScreen()
                    is SplashViewModel.SplashEvent.ShowOverviewScreen -> navigateToOverviewScreen(
                        event.token, event.username, event.roleName)
                    is SplashViewModel.SplashEvent.ShowProductsScreen -> navigateToProductsScreen(
                        event.token, event.username, event.roleName)
                }
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.checkLoginStatus(it)
        }
//        val action = SplashFragmentDirections.actionSplashFragmentToCustomerFragment()
//        findNavController().navigate(action)
    }

    private fun navigateToLoginScreen() {
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun navigateToOverviewScreen(token: String, username: String, roleName: String) {
        val intent = Intent(requireContext(), SaleActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("username", username)
        intent.putExtra("role_name", roleName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToProductsScreen(token: String, username: String, roleName: String) {
        val intent = Intent(requireContext(), CustomerActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("username", username)
        intent.putExtra("role_name", roleName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
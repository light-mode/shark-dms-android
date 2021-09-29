package vn.sharkdms.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel

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
                        event.token)
                    is SplashViewModel.SplashEvent.ShowProductsScreen -> navigateToProductsScreen(
                        event.token)
                }
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.checkLoginStatus(it)
        }
    }

    private fun navigateToLoginScreen() {
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun navigateToOverviewScreen(token: String) {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java).token = token
        val action = SplashFragmentDirections.actionSplashFragmentToOverviewFragment()
        findNavController().navigate(action)
    }

    private fun navigateToProductsScreen(token: String) {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java).token = token
        val action = SplashFragmentDirections.actionSplashFragmentToProductsFragment()
        findNavController().navigate(action)
    }
}
package vn.sharkdms.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.activity.CustomerActivity
import vn.sharkdms.R
import vn.sharkdms.activity.SaleActivity
import vn.sharkdms.util.Utils

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = requireContext()
        val sharedPrefsName = context.getString(R.string.shared_prefs_private)
        val sharedPrefs = context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val showSplashScreenKey = getString(R.string.show_splash_screen_key)
        val showSplashScreen = sharedPrefs.getBoolean(showSplashScreenKey, false)
        if (showSplashScreen) {
            val editor = sharedPrefs.edit()
            editor.putBoolean(getString(R.string.show_splash_screen_key), false)
            editor.apply()
        }
        viewModel.showSplashScreen = showSplashScreen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.splashEvent.collect { event ->
                when (event) {
                    is SplashViewModel.SplashEvent.ShowLoginScreen -> navigateToLoginScreen()
                    is SplashViewModel.SplashEvent.ShowOverviewScreen -> navigateToOverviewScreen(
                        event.token, event.username, event.roleName)
                    is SplashViewModel.SplashEvent.ShowProductsScreen -> navigateToProductsScreen(
                        event.token, event.username, event.roleName, event.avatar)
                }
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.checkLoginStatus(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
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

    private fun navigateToProductsScreen(token: String, username: String, roleName: String, avatar: String) {
        val intent = Intent(requireContext(), CustomerActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("username", username)
        intent.putExtra("role_name", roleName)
        intent.putExtra("avatar", avatar)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
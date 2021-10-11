package vn.sharkdms.ui.logout

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.MainActivity
import vn.sharkdms.R

@AndroidEntryPoint
class LogoutDialogFragment : DialogFragment(R.layout.fragment_logout_dialog) {

    private val viewModel by viewModels<LogoutViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.logoutEvent.collect { event ->
                when (event) {
                    is LogoutViewModel.LogoutEvent.ShowLoginScreen -> navigateToLoginScreen()
                }
            }
        }
        viewModel.deleteUserInfo()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
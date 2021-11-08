package vn.sharkdms.ui.logout

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.MainActivity
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentLogoutDialogBinding

@AndroidEntryPoint
class LogoutDialogFragment : DialogFragment(R.layout.fragment_logout_dialog) {

    private val viewModel by viewModels<LogoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_logout_dialog, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.logoutEvent.collect { event ->
                when (event) {
                    is LogoutViewModel.LogoutEvent.ShowLoginScreen -> navigateToLoginScreen()
                }
            }
        }
        val binding = FragmentLogoutDialogBinding.bind(view)
        binding.apply {
            buttonNo.setOnClickListener {
                dismiss()
            }
            buttonYes.setOnClickListener {
                viewModel.deleteUserInfo()
            }
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
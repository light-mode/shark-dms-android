package vn.sharkdms.ui.logout

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.activity.MainActivity
import vn.sharkdms.R
import vn.sharkdms.databinding.DialogLogoutBinding
import vn.sharkdms.util.Utils

@AndroidEntryPoint
class LogoutDialogFragment : DialogFragment(R.layout.dialog_logout) {

    private val viewModel by viewModels<LogoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_logout, container, false)
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
        val binding = DialogLogoutBinding.bind(view)
        binding.apply {
            buttonNo.setOnClickListener {
                dismiss()
            }
            buttonYes.setOnClickListener {
                viewModel.deleteUserInfo()
            }
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
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
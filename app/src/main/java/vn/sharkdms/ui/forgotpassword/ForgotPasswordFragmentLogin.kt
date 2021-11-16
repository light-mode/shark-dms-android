package vn.sharkdms.ui.forgotpassword

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.util.MessageDialog

@AndroidEntryPoint
class ForgotPasswordFragmentLogin : ForgotPasswordFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(MessageDialog.FORGOT_PASSWORD) { _, bundle ->
            val result = bundle.getInt(MessageDialog.DIALOG_RESULT)
            if (result == Dialog.BUTTON_POSITIVE) {
                findNavController().popBackStack(R.id.forgotPasswordFragment, true)
            }
        }
    }

    override fun showMessageDialog(message: String) {
        val action = ForgotPasswordFragmentLoginDirections.actionGlobalMessageDialog2(message)
        findNavController().navigate(action)
    }
}
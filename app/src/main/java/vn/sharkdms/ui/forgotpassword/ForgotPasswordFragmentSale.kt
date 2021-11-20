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
class ForgotPasswordFragmentSale : ForgotPasswordFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(MessageDialog.TAG) { _, bundle ->
            if (Dialog.BUTTON_POSITIVE == bundle.getInt(MessageDialog.FORGOT_PASSWORD)) {
                findNavController().popBackStack(R.id.forgotPasswordFragment2, true)
            }
        }
    }

    override fun showMessageDialog(message: String) {
        val action = ForgotPasswordFragmentSaleDirections.actionGlobalMessageDialog3(message, MessageDialog.FORGOT_PASSWORD)
        findNavController().navigate(action)
    }
}
package vn.sharkdms.ui.password.forgot

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.util.MessageDialog

@AndroidEntryPoint
class ForgotPasswordFragmentCustomer : ForgotPasswordFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(MessageDialog.TAG) { _, bundle ->
            if (Dialog.BUTTON_POSITIVE == bundle.getInt(MessageDialog.FORGOT_PASSWORD)) {
                findNavController().popBackStack(R.id.forgotPasswordFragment3, true)
            }
        }
    }

    override fun showMessageDialog(message: String) {
        val action = ForgotPasswordFragmentCustomerDirections.actionGlobalMessageDialog(message, MessageDialog.FORGOT_PASSWORD)
        findNavController().navigate(action)
    }
}
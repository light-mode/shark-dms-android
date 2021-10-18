package vn.sharkdms.ui.customer.gallery

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_discount_dialog.view.*
import vn.sharkdms.R

class SuccessDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_upload_success_dialog, container, false)

        setBtnCloseOnClickListener(rootView)

        return rootView
    }

    override fun onDismiss(dialog: DialogInterface) {
        findNavController().navigateUp()
    }

    private fun setBtnCloseOnClickListener(rootView: View) {
        rootView.apply {
            iv_close.setOnClickListener {
                dismiss()
            }
        }
    }
}
package vn.sharkdms.util

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import vn.sharkdms.R
import vn.sharkdms.databinding.DialogConfirmBinding

class ConfirmDialog : DialogFragment() {
    companion object {
        const val TAG = "ConfirmDialog"
        const val REMOVE_ITEM = "REMOVE_ITEM"
        const val CANCEL_ORDER = "CANCEL_ORDER"
        const val CREATE_ORDER = "CREATE_ORDER"
        const val SEND_REPORT = "SEND_REPORT"
    }

    private val args by navArgs<ConfirmDialogArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogConfirmBinding.bind(view)
        binding.apply {
            textViewTitle.text = args.title
            textViewMessage.text = args.message
            buttonNegative.setOnClickListener {
                setFragmentResult(TAG, bundleOf(args.resultKey to Dialog.BUTTON_NEGATIVE))
                dismiss()
            }
            buttonPositive.setOnClickListener {
                setFragmentResult(TAG, bundleOf(args.resultKey to Dialog.BUTTON_POSITIVE))
                dismiss()
            }
        }
        isCancelable = false
    }
}
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
        const val REMOVE_ITEM = "REMOVE_ITEM"
        const val DIALOG_RESULT = "DIALOG_RESULT"
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
                setFragmentResult(REMOVE_ITEM, bundleOf(DIALOG_RESULT to Dialog.BUTTON_NEGATIVE))
                dismiss()
            }
            buttonPositive.setOnClickListener {
                setFragmentResult(REMOVE_ITEM, bundleOf(DIALOG_RESULT to Dialog.BUTTON_POSITIVE))
                dismiss()
            }
        }
        isCancelable = false
    }
}
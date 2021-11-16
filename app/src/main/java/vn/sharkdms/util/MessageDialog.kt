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
import vn.sharkdms.databinding.DialogMessageBinding

class MessageDialog : DialogFragment() {
    companion object {
        const val FORGOT_PASSWORD = "FORGOT_PASSWORD"
        const val DIALOG_RESULT = "DIALOG_RESULT"
    }

    private val args by navArgs<MessageDialogArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogMessageBinding.bind(view)
        binding.apply {
            textViewMessage.text = args.message
            buttonConfirm.setOnClickListener {
                setFragmentResult(FORGOT_PASSWORD,
                    bundleOf(DIALOG_RESULT to Dialog.BUTTON_POSITIVE))
                dismiss()
            }
        }
        isCancelable = false
    }
}
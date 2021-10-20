package vn.sharkdms.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import vn.sharkdms.R
import vn.sharkdms.databinding.DialogMessageBinding

class MessageDialog(private val listener: MessageDialogListener,
    private val message: String) : DialogFragment(R.layout.dialog_message) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_message, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogMessageBinding.bind(view)
        binding.apply {
            textViewMessage.text = message
            buttonConfirm.setOnClickListener {
                listener.onConfirmButtonClick()
                dismiss()
            }
        }
        isCancelable = false
    }

    interface MessageDialogListener {
        fun onConfirmButtonClick()
    }
}
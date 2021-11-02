package vn.sharkdms.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import vn.sharkdms.R
import vn.sharkdms.databinding.DialogConfirmBinding

class ConfirmDialog(private val listener: ConfirmDialogListener, private val title: String,
    private val message: String) : DialogFragment(R.layout.dialog_confirm) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_confirm, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogConfirmBinding.bind(view)
        binding.apply {
            textViewTitle.text = title
            textViewMessage.text = message
            buttonNegative.setOnClickListener {
                listener.onNegativeButtonClick()
                dismiss()
            }
            buttonPositive.setOnClickListener {
                listener.onPositiveButtonClick()
                dismiss()
            }
        }
        isCancelable = false
    }

    interface ConfirmDialogListener {
        fun onNegativeButtonClick()
        fun onPositiveButtonClick()
    }
}
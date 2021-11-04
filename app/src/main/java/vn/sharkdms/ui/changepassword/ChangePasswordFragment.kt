package vn.sharkdms.ui.changepassword

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.databinding.FragmentChangePasswordBinding

@AndroidEntryPoint
class ChangePasswordFragment : BaseChangePasswordFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChangePasswordBinding.bind(view)
        setForgotPasswordTextViewListener(binding)
    }

    private fun setForgotPasswordTextViewListener(binding: FragmentChangePasswordBinding) {
        binding.textViewForgotPassword.setOnClickListener {
            val action = ChangePasswordFragmentDirections
                .actionChangePasswordFragmentToForgotPasswordFragment2()
            findNavController().navigate(action)
        }
    }
}
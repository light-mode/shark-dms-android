package vn.sharkdms.ui.password.change

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import vn.sharkdms.databinding.FragmentChangePasswordBinding

class ChangePasswordFragmentCustomer : ChangePasswordFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChangePasswordBinding.bind(view)
        setForgotPasswordTextViewListener(binding)
    }

    private fun setForgotPasswordTextViewListener(binding: FragmentChangePasswordBinding) {
        binding.textViewForgotPassword.setOnClickListener {
            val action = ChangePasswordFragmentCustomerDirections
                .actionChangePasswordFragment2ToForgotPasswordFragment3()
            findNavController().navigate(action)
        }
    }
}
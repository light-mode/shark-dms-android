package vn.sharkdms.ui.changepasswordcustomer

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import vn.sharkdms.databinding.FragmentChangePasswordBinding
import vn.sharkdms.ui.changepassword.BaseChangePasswordFragment

class CustomerChangePasswordFragment : BaseChangePasswordFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChangePasswordBinding.bind(view)
        setForgotPasswordTextViewListener(binding)
    }

    private fun setForgotPasswordTextViewListener(binding: FragmentChangePasswordBinding) {
        binding.textViewForgotPassword.setOnClickListener {
            val action = CustomerChangePasswordFragmentDirections
                .actionChangePasswordFragment2ToForgotPasswordFragment3()
            findNavController().navigate(action)
        }
    }
}
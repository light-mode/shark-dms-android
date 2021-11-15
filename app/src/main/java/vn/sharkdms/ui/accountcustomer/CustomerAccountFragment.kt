package vn.sharkdms.ui.accountcustomer

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.ui.base.account.BaseAccountFragment
import vn.sharkdms.ui.logout.LogoutDialogFragment

@AndroidEntryPoint
class CustomerAccountFragment : BaseAccountFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAccountBinding.bind(view)
        binding.apply {
            tvAccountTitle.text = getString(R.string.fragment_customer_info_title)
            tvAccountDiscount.text = getString(
                R.string.fragment_customer_account_text_view_discount_text)
            cardViewLogOut.visibility = View.VISIBLE
        }
        setAvatarImageViewListener(binding)
        setChangePasswordCardViewListener(binding)
        setLogoutCardViewListener(binding)
    }

    private fun setAvatarImageViewListener(binding: FragmentAccountBinding) {
        binding.imageViewAvatar.setOnClickListener {
            val action = CustomerAccountFragmentDirections.actionGlobalImageChooserDialog2()
            findNavController().navigate(action)
        }
    }

    private fun setChangePasswordCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewChangePassword.setOnClickListener {
            val action = CustomerAccountFragmentDirections
                .actionCustomerAccountFragmentToChangePasswordFragment2()
            findNavController().navigate(action)
        }
    }

    private fun setLogoutCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewLogOut.setOnClickListener {
            LogoutDialogFragment().show(requireActivity().supportFragmentManager, "")
        }
    }
}
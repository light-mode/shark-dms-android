package vn.sharkdms.ui.account

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.ui.customer.discount.DiscountDialogFragment
import vn.sharkdms.ui.logout.LogoutDialogFragment

@AndroidEntryPoint
class AccountFragmentCustomer : AccountFragment() {

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
        setDiscountCardViewListener(binding)
        setLogoutCardViewListener(binding)
    }

    private fun setAvatarImageViewListener(binding: FragmentAccountBinding) {
        binding.imageViewAvatar.setOnClickListener {
            val action = AccountFragmentCustomerDirections.actionGlobalImageChooserDialog2()
            findNavController().navigate(action)
        }
    }

    private fun setChangePasswordCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewChangePassword.setOnClickListener {
            val action = AccountFragmentCustomerDirections
                .actionCustomerAccountFragmentToChangePasswordFragment2()
            findNavController().navigate(action)
        }
    }

    private fun setDiscountCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewDiscount.setOnClickListener {
            val dialog = DiscountDialogFragment().newInstance(discountInfo, 0)
            dialog.show(childFragmentManager, TAG)
        }
    }

    private fun setLogoutCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewLogOut.setOnClickListener {
            LogoutDialogFragment().show(requireActivity().supportFragmentManager, "")
        }
    }
}
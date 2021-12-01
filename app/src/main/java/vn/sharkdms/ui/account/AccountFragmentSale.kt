package vn.sharkdms.ui.account

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.ui.customer.discount.DiscountDialogFragment

@AndroidEntryPoint
class AccountFragmentSale : AccountFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAccountBinding.bind(view)
        setChangePasswordCardViewListener(binding)
        setDiscountCardViewListener(binding)
        setAvatarImageViewListener(binding)

    }

    private fun setChangePasswordCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewChangePassword.setOnClickListener {
            val action = AccountFragmentSaleDirections.actionAccountFragmentToChangePasswordFragment()
            findNavController().navigate(action)
        }
    }

    private fun setDiscountCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewDiscount.setOnClickListener {
            val dialog = DiscountDialogFragment().newInstance(discountInfo, 1)
            dialog.show(childFragmentManager, TAG)
        }
    }

    private fun setAvatarImageViewListener(binding: FragmentAccountBinding) {
        binding.imageViewAvatar.setOnClickListener {
            val action = AccountFragmentSaleDirections.actionGlobalImageChooserDialog()
            findNavController().navigate(action)
        }
    }
}
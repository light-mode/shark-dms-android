package vn.sharkdms.ui.account

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.ui.customer.discount.DiscountDialogFragment
import vn.sharkdms.ui.customer.discount.DiscountDialogViewModelCustomer
import vn.sharkdms.ui.customer.discount.DiscountDialogViewModelSale
import vn.sharkdms.ui.customer.discount.DiscountInfo
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.Utils

@AndroidEntryPoint
class AccountFragmentSale : AccountFragment() {

    private lateinit var discountViewModelSale: DiscountDialogViewModelSale
    private var discountInfo: String = ""
    private var discountMinAmount: String? = ""
    private var discountMaxAmount: String? = ""
    private var discountRate: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAccountBinding.bind(view)
        discountViewModelSale = ViewModelProvider(
            requireActivity())[DiscountDialogViewModelSale::class.java]
        discountViewModelSale.sendGetCompanyDiscountInfo(sharedViewModel.token)
        setChangePasswordCardViewListener(binding)
        setDiscountCardViewListener(binding)
        setAvatarImageViewListener(binding)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            discountViewModelSale.discountDialogEvent.collect { event ->
                when (event) {
                    is DiscountDialogViewModelSale.DiscountDialogEvent.OnResponse -> {
                        if (event.data?.size != 0) handleGetDiscountInfoResponse(event.code,
                            event.message, event.data?.get(event.data.size - 1))
                        else handleGetDiscountInfoResponse(event.code, event.message, null)
                    }
                    is DiscountDialogViewModelSale.DiscountDialogEvent.OnFailure ->
                        Utils.showConnectivityOffMessage(requireContext())
                    is DiscountDialogViewModelSale.DiscountDialogEvent.ShowUnauthorizedDialog ->
                        Utils.showUnauthorizedDialog(requireActivity())
                }
            }
        }
    }

    private fun setChangePasswordCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewChangePassword.setOnClickListener {
            val action = AccountFragmentSaleDirections.actionAccountFragmentToChangePasswordFragment()
            findNavController().navigate(action)
        }
    }

    private fun setDiscountCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewDiscount.setOnClickListener {
            val dialog = DiscountDialogFragment().newInstance(discountInfo, discountMinAmount, discountMaxAmount, discountRate, 1)
            dialog.show(childFragmentManager, TAG)
        }
    }

    private fun setAvatarImageViewListener(binding: FragmentAccountBinding) {
        binding.imageViewAvatar.setOnClickListener {
            val action = AccountFragmentSaleDirections.actionGlobalImageChooserDialog()
            findNavController().navigate(action)
        }
    }

    private fun handleGetDiscountInfoResponse(code: Int, message: String, data: DiscountInfo?) {
        when (code) {
            HttpStatus.OK -> {
                if (data != null) {
                    discountInfo = data.ruleCode
                    discountMaxAmount = data.maxAmount.toString()
                    discountMinAmount = data.minAmount.toString()
                    discountRate = data.discountRate.toString()
                }
            }
            HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> {
                Log.e(TAG, message)
            }
            else -> Log.e(TAG, code.toString())
        }
    }
}
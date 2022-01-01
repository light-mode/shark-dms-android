package vn.sharkdms.ui.account

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.ui.customer.discount.DiscountDialogFragment
import vn.sharkdms.ui.customer.discount.DiscountDialogViewModelCustomer
import vn.sharkdms.ui.logout.LogoutDialogFragment
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.Utils
import kotlinx.coroutines.flow.collect
import vn.sharkdms.api.DiscountInfo

@AndroidEntryPoint
class AccountFragmentCustomer : AccountFragment() {

    private lateinit var discountViewModelCustomer: DiscountDialogViewModelCustomer
    private var infoMin: DiscountInfo? = null
    private var infoMinMax: DiscountInfo? = null
    private var infoMax: DiscountInfo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAccountBinding.bind(view)
        discountViewModelCustomer = ViewModelProvider(
            requireActivity())[DiscountDialogViewModelCustomer::class.java]
        if (sharedViewModel.connectivity.value == true) {
            discountViewModelCustomer.sendGetCompanyDiscountInfo(sharedViewModel.token)
        }
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
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            discountViewModelCustomer.discountDialogEvent.collect { event ->
                when (event) {
                    is DiscountDialogViewModelCustomer.DiscountDialogEvent.OnResponse -> {
                        if (event.data?.size != 0) handleGetDiscountInfoResponse(event.code,
                            event.message, event.data)
                        else handleGetDiscountInfoResponse(event.code, event.message, null)
                    }
                    is DiscountDialogViewModelCustomer.DiscountDialogEvent.OnFailure ->
                        Utils.showConnectivityOffMessage(requireContext())
                    is DiscountDialogViewModelCustomer.DiscountDialogEvent.ShowUnauthorizedDialog ->
                        Utils.showUnauthorizedDialog(requireActivity())
                }
            }
        }
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
            val dialog = DiscountDialogFragment().newInstance(infoMin, infoMinMax, infoMax, 2)
            dialog.show(childFragmentManager, TAG)
        }
    }

    private fun setLogoutCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewLogOut.setOnClickListener {
            LogoutDialogFragment().show(requireActivity().supportFragmentManager, "")
        }
    }

    private fun handleGetDiscountInfoResponse(code: Int, message: String, data: List<DiscountInfo>?) {
        when (code) {
            HttpStatus.OK -> {
                if (data != null) {
                    for (info in data) {
                        when (info.ruleCode) {
                            "max" -> infoMax = info
                            "min_max" -> infoMinMax = info
                            "min" -> infoMin = info
                        }
                    }
                }
            }
            HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> {
                Log.e(TAG, message)
            }
            else -> Log.e(TAG, code.toString())
        }
    }
}
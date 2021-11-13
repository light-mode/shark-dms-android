package vn.sharkdms.ui.base.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.data.User
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.ui.account.AccountFragmentDirections
import vn.sharkdms.ui.customer.discount.DiscountDialogFragment
import vn.sharkdms.ui.customer.discount.DiscountDialogViewModel
import vn.sharkdms.ui.customer.discount.DiscountInfo
import vn.sharkdms.ui.customer.info.CheckInViewModel
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.Utils

open class BaseAccountFragment : Fragment(R.layout.fragment_account) {
    private val TAG = "AccountFragment"

    private val viewModel by viewModels<AccountViewModel>()
    private lateinit var discountViewModel: DiscountDialogViewModel
    private var userId = 1
    private var discountInfo: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAccountBinding.bind(view)
        discountViewModel = ViewModelProvider(requireActivity())[DiscountDialogViewModel::class.java]
        binding.apply {
            iconBack.setOnClickListener {
                findNavController().navigateUp()
            }
            cardViewDiscount.setOnClickListener {
                val dialog = DiscountDialogFragment().newInstance(discountInfo)
                dialog.show(childFragmentManager, TAG)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.accountEvent.collect { event ->
                when (event) {
                    is AccountViewModel.AccountEvent.BindUserInfoView -> {
                        bindUserInfoView(binding, event.user)
                        userId = event.user.id
                    }
                }
            }
            discountViewModel.discountDialogEvent.collect { event ->
                when(event) {
                    is DiscountDialogViewModel.DiscountDialogEvent.OnResponse -> {
                        if (event.data?.size  != 0)
                            handleGetDiscountInfoResponse(event.code, event.message, event.data?.get(0))
                        else
                            handleGetDiscountInfoResponse(event.code, event.message, null)
                    }
                    is DiscountDialogViewModel.DiscountDialogEvent.OnFailure ->
                        handleGetDiscountFailure()
                    is DiscountDialogViewModel.DiscountDialogEvent.ShowUnauthorizedDialog ->
                        Utils.showUnauthorizedDialog(requireActivity())
                }
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.getUserInfo(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun bindUserInfoView(binding: FragmentAccountBinding, user: User) {
        binding.apply {
            Glide.with(this@BaseAccountFragment).load(user.avatar).error(R.drawable.ic_avatar)
                .into(imageViewAvatar)
            textViewName.text = user.name
            textViewPhone.text = user.phone
            textViewUsername.text = user.username
            textViewEmail.text = user.email
            textViewCompany.text = user.company
            textViewPosition.text = user.position
        }
    }

    private fun handleGetDiscountInfoResponse(code: Int, message: String, data: DiscountInfo?) {
        when (code) {
            HttpStatus.OK -> {
                if (data != null) discountInfo = data.ruleCode
            }
            HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> {
                Log.e(TAG, message)
            }
            else -> Log.e(TAG, code.toString())
        }
    }

    private fun handleGetDiscountFailure() {
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }
}
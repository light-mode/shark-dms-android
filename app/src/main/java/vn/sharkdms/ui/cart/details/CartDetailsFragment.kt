package vn.sharkdms.ui.cart.details

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentCartDetailsBinding
import vn.sharkdms.ui.cart.Cart
import vn.sharkdms.ui.cart.CartItem
import vn.sharkdms.ui.customer.list.Customer
import vn.sharkdms.util.ConfirmDialogFragment
import vn.sharkdms.util.Formatter
import vn.sharkdms.util.Utils

@AndroidEntryPoint
abstract class CartDetailsFragment : Fragment(
    R.layout.fragment_cart_details), CartItemAdapter.OnItemClickListener {

    private var customer: Customer? = null
    private val viewModel by viewModels<CartDetailsViewModel>()
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        if (viewModel.cart == null && !viewModel.removeLastItem) viewModel.cart = getCartFromArgs()
        customer = getCustomerFromArgs()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun getCartFromArgs(): Cart?
    abstract fun getCustomerFromArgs(): Customer?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCartDetailsBinding.bind(view)
        bind(binding)
        setListener(binding)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        Utils.setupUI(binding.cartDetailsFragment, requireActivity() as AppCompatActivity)
        setFragmentResultListener(ConfirmDialogFragment.TAG) { _, bundle ->
            if (sharedViewModel.connectivity.value != true) {
                Utils.showConnectivityOffMessage(requireContext())
                return@setFragmentResultListener
            }
            when (Dialog.BUTTON_POSITIVE) {
                bundle.getInt(ConfirmDialogFragment.REMOVE_ITEM) -> {
                    doBeforeRemoveFromCartRequest(binding)
                    viewModel.removeFromCart(sharedViewModel.token, customer)
                }
                bundle.getInt(ConfirmDialogFragment.CANCEL_ORDER) -> {
                    doBeforeCancelOrderRequest(binding)
                    viewModel.cancelOrder(sharedViewModel.token)
                }
                bundle.getInt(ConfirmDialogFragment.CREATE_ORDER) -> {
                    doBeforeCreateOrderRequest(binding)
                    val discount = binding.editTextDiscount.text.toString()
                    val note = binding.editTextNote.text.toString()
                    viewModel.createOrder(sharedViewModel.token, discount, note, customer)
                }
            }
        }
    }

    private fun doBeforeRemoveFromCartRequest(binding: FragmentCartDetailsBinding) {
        binding.apply {
            buttonCreate.isEnabled = false
            buttonContinue.isEnabled = false
            buttonCancel.isEnabled = false
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun bind(binding: FragmentCartDetailsBinding) {
        bindCustomerInfo(binding)
        if (viewModel.cart == null) {
            binding.apply {
                iconNoCartItem.visibility = View.VISIBLE
                textViewNoCartItem.visibility = View.VISIBLE
                buttonCancel.isEnabled = false
                buttonCancel.setBackgroundResource(R.drawable.button_disable_square)
                buttonCreate.isEnabled = false
                buttonCreate.setBackgroundResource(R.drawable.button_disable_square)
                bindCartInfo(binding, 0, 0)
            }
        } else viewModel.cart?.let {
            bindCartItems(binding, it.items)
            bindCartInfo(binding, it.discountAmount, it.totalAmount)
        }
    }

    private fun bindCustomerInfo(binding: FragmentCartDetailsBinding) {
        binding.apply {
            if (customer == null) {
                imageViewAvatar.visibility = View.GONE
                layoutCustomerInfo.visibility = View.GONE
                textViewDiscount.visibility = View.GONE
                editTextDiscount.visibility = View.GONE
                textViewDiscountGeneral.visibility = View.INVISIBLE
                textViewDiscountGeneralValue.visibility = View.INVISIBLE
                buttonCancel.visibility = View.GONE
            } else {
                Glide.with(requireContext()).load(customer!!.customerAvatar)
                    .error(R.drawable.avatar_create_customer).into(imageViewAvatar)
                textViewName.text = customer!!.customerName
                textViewPhone.text = customer!!.customerPhone
            }
        }
    }

    private fun bindCartItems(binding: FragmentCartDetailsBinding, items: List<CartItem>) {
        val adapter = CartItemAdapter(items, this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setAdapter(adapter)
        }
    }

    private fun bindCartInfo(binding: FragmentCartDetailsBinding, discountGeneral: Long,
        totalAmount: Long) {
        binding.apply {
            textViewDiscountGeneralValue.text = getString(
                R.string.fragment_cart_details_currency_format,
                Formatter.formatCurrency(discountGeneral.toString()))
            textViewTotalAmountValue.text = getString(
                R.string.fragment_cart_details_currency_format,
                Formatter.formatCurrency(totalAmount.toString()))
        }
    }

    private fun setListener(binding: FragmentCartDetailsBinding) {
        setBackIconListener(binding)
        setCancelButtonListener(binding)
        setContinueButtonListener(binding)
        setCreateButtonListener(binding)
        setEventListener(binding)
    }

    private fun setBackIconListener(binding: FragmentCartDetailsBinding) {
        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setCancelButtonListener(binding: FragmentCartDetailsBinding) {
        binding.buttonCancel.setOnClickListener {
            val title = getString(R.string.fragment_cart_details_dialog_cancel_order_title)
            val message = getString(R.string.fragment_cart_details_dialog_cancel_order_message)
            val action = if (customer == null) {
                CartDetailsFragmentCustomerDirections.actionGlobalConfirmDialog(title, message, ConfirmDialogFragment.CANCEL_ORDER)
            } else CartDetailsFragmentSaleDirections.actionGlobalConfirmDialog2(title, message, ConfirmDialogFragment.CANCEL_ORDER)
            findNavController().navigate(action)
        }
    }

    private fun doBeforeCancelOrderRequest(binding: FragmentCartDetailsBinding) {
        binding.apply {
            buttonCancel.isEnabled = false
            buttonContinue.isEnabled = false
            buttonCreate.isEnabled = false
            buttonCancel.text = ""
            progressBarCancel.visibility = View.VISIBLE
        }
    }

    private fun setContinueButtonListener(binding: FragmentCartDetailsBinding) {
        binding.buttonContinue.setOnClickListener {
            if (sharedViewModel.connectivity.value != true) {
                Utils.showConnectivityOffMessage(requireContext())
                return@setOnClickListener
            }
            findNavController().navigateUp()
        }
    }

    private fun setCreateButtonListener(binding: FragmentCartDetailsBinding) {
        binding.buttonCreate.setOnClickListener {
            val title = getString(R.string.fragment_cart_details_dialog_create_order_title)
            val message = getString(R.string.fragment_cart_details_dialog_create_order_message)
            val action = if (customer == null) {
                CartDetailsFragmentCustomerDirections.actionGlobalConfirmDialog(title, message, ConfirmDialogFragment.CREATE_ORDER)
            } else CartDetailsFragmentSaleDirections.actionGlobalConfirmDialog2(title, message, ConfirmDialogFragment.CREATE_ORDER)
            findNavController().navigate(action)
        }
    }

    private fun doBeforeCreateOrderRequest(binding: FragmentCartDetailsBinding) {
        binding.apply {
            buttonCreate.isEnabled = false
            buttonContinue.isEnabled = false
            buttonCancel.isEnabled = false
            buttonCreate.text = ""
            progressBarCreate.visibility = View.VISIBLE
        }
    }

    private fun setEventListener(binding: FragmentCartDetailsBinding) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.cartDetailsEvent.collect { event ->
                when (event) {
                    is CartDetailsViewModel.CartDetailsEvent.OnRemoveItemSuccess -> {
                        doAfterRemoveFromCartResponse(binding)
                        val cart = event.cart
                        bindCartItems(binding, cart.items)
                        bindCartInfo(binding, cart.discountAmount, cart.totalAmount)
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is CartDetailsViewModel.CartDetailsEvent.OnRemoveLastItemSuccess -> {
                        sharedViewModel.cartId.value = 0
                        binding.apply {
                            buttonCreate.isEnabled = false
                            buttonCreate.setBackgroundResource(R.drawable.button_disable_square)
                            buttonCancel.isEnabled = false
                            buttonCancel.setBackgroundResource(R.drawable.button_disable_square)
                            iconNoCartItem.visibility = View.VISIBLE
                            textViewNoCartItem.visibility = View.VISIBLE
                            buttonContinue.isEnabled = true
                        }
                        bindCartItems(binding, emptyList())
                        bindCartInfo(binding, 0, 0)
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is CartDetailsViewModel.CartDetailsEvent.OnCancelOrderSuccess -> {
                        doAfterCancelOrderResponse(binding)
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                        val action = CartDetailsFragmentSaleDirections
                            .actionCartDetailsFragmentToCustomerInfoFragment(
                                customer!!)
                        findNavController().navigate(action)
                    }
                    is CartDetailsViewModel.CartDetailsEvent.OnCreateOrderSuccess -> {
                        sharedViewModel.cartId.value = 0
                        doAfterCreateOrderResponse(binding)
                        val action = if (customer == null) CartDetailsFragmentCustomerDirections
                            .actionCartDetailsFragment2ToOrderResultFragment2(
                                event.data)
                        else CartDetailsFragmentSaleDirections
                            .actionCartDetailsFragmentToOrderResultFragment(
                                event.data)
                        findNavController().navigate(action)
                    }
                    is CartDetailsViewModel.CartDetailsEvent.ShowBadRequestErrorMessage -> {
                        doAfterCancelOrderResponse(binding)
                        doAfterCreateOrderResponse(binding)
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is CartDetailsViewModel.CartDetailsEvent.ShowNetworkConnectionErrorMessage -> {
                        doAfterCancelOrderResponse(binding)
                        doAfterCreateOrderResponse(binding)
                        Utils.showConnectivityOffMessage(requireContext())
                    }
                    is CartDetailsViewModel.CartDetailsEvent.ShowUnauthorizedDialog -> {
                        Utils.showUnauthorizedDialog(requireActivity())
                    }
                }
            }
        }
    }

    private fun doAfterRemoveFromCartResponse(binding: FragmentCartDetailsBinding) {
        binding.apply {
            buttonCreate.isEnabled = true
            buttonContinue.isEnabled = true
            buttonCancel.isEnabled = true
        }
    }

    private fun doAfterCancelOrderResponse(binding: FragmentCartDetailsBinding) {
        binding.apply {
            progressBarCancel.visibility = View.GONE
            buttonCancel.text = getString(R.string.fragment_cart_details_button_cancel_text)
            buttonCreate.isEnabled = true
            buttonContinue.isEnabled = true
            buttonCancel.isEnabled = true
        }
    }

    private fun doAfterCreateOrderResponse(binding: FragmentCartDetailsBinding) {
        binding.apply {
            progressBarCreate.visibility = View.GONE
            buttonCreate.text = getString(R.string.fragment_cart_details_button_create_text)
            buttonCancel.isEnabled = true
            buttonContinue.isEnabled = true
            buttonCreate.isEnabled = true
        }
    }

    override fun onRemoveItemClick(item: CartItem) {
        viewModel.cartItemId = item.id
        val title = getString(R.string.fragment_cart_details_dialog_remove_product_title)
        val message = getString(R.string.fragment_cart_details_dialog_remove_product_message)
        val action = if (customer == null) {
            CartDetailsFragmentCustomerDirections.actionGlobalConfirmDialog(title, message, ConfirmDialogFragment.REMOVE_ITEM)
        } else CartDetailsFragmentSaleDirections.actionGlobalConfirmDialog2(title, message, ConfirmDialogFragment.REMOVE_ITEM)
        findNavController().navigate(action)
    }
}
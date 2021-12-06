package vn.sharkdms.ui.cart.add

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentAddToCartBinding
import vn.sharkdms.ui.cart.Cart
import vn.sharkdms.ui.customer.list.Customer
import vn.sharkdms.ui.product.Product
import vn.sharkdms.util.Formatter
import vn.sharkdms.util.Utils

@AndroidEntryPoint
abstract class AddToCartFragment : Fragment(R.layout.fragment_add_to_cart) {

    private lateinit var product: Product
    private var customer: Customer? = null
    private val viewModel by viewModels<AddToCartViewModel>()
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        product = getProductFromArgs()
        customer = getCustomerFromArgs()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun getProductFromArgs(): Product
    abstract fun getCustomerFromArgs(): Customer?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddToCartBinding.bind(view)
        bind(binding)
        setBackIconListener(binding)
        setQuantityEditTextListener(binding)
        setMinusButtonListener(binding)
        setPlusButtonListener(binding)
        setAddButtonListener(binding)
        setEventListener(binding)
        viewModel.currentQuantity.observe(viewLifecycleOwner) { quantity ->
            binding.apply {
                if (quantity == 0L) {
                    buttonAdd.isEnabled = false
                    buttonAdd.setBackgroundResource(R.drawable.button_disable)
                } else {
                    buttonAdd.isEnabled = true
                    buttonAdd.setBackgroundResource(R.drawable.button_primary)
                }
            }
        }
        viewModel.currentTotalPrice.observe(viewLifecycleOwner) { totalPrice ->
            val formattedTotalPrice = getString(R.string.fragment_add_to_cart_currency_format,
                Formatter.formatCurrency(totalPrice.toString()))
            binding.textViewTotalPrice.text = formattedTotalPrice
        }
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        Utils.setupUI(binding.addToCartFragment, requireActivity() as AppCompatActivity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun bind(binding: FragmentAddToCartBinding) {
        binding.apply {
            Glide.with(requireContext()).load(product.imageUrl).error(R.drawable.ic_product)
                .into(imageViewProduct)
            textViewName.text = product.name
            textViewAvailable.text = product.quantity.toString()
            editTextQuantity.setText("0")
            textViewPrice.text = getString(R.string.fragment_add_to_cart_currency_format,
                Formatter.formatCurrency(product.price.toString()))
            textViewTotalPrice.text = (product.price * 0).toString()
        }
    }

    private fun setBackIconListener(binding: FragmentAddToCartBinding) {
        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setQuantityEditTextListener(binding: FragmentAddToCartBinding) {
        binding.editTextQuantity.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                    after: Int) { //
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                    count: Int) { //
                }

                override fun afterTextChanged(s: Editable?) {
                    val value = if (s.isNullOrEmpty()) 0
                    else s.toString().toLong()
                    val remain = product.quantity
                    if (value > remain) setText(remain.toString())
                    else {
                        val origin = s.toString()
                        val truncated = origin.replaceFirst("^0+(?!$)".toRegex(), "")
                        if (origin.length != truncated.length) setText(truncated)
                    }
                    viewModel.setQuantity(value.toString(), product.price, product.quantity)
                }
            })
        }
    }

    private fun setMinusButtonListener(binding: FragmentAddToCartBinding) {
        binding.buttonMinus.setOnClickListener {
            viewModel.minusOneToQuantity(product.price)
        }
    }

    private fun setPlusButtonListener(binding: FragmentAddToCartBinding) {
        binding.buttonPlus.setOnClickListener {
            viewModel.plusOneToQuantity(product.price, product.quantity)
        }
    }

    private fun setAddButtonListener(binding: FragmentAddToCartBinding) {
        binding.apply {
            buttonAdd.setOnClickListener {
                if (sharedViewModel.connectivity.value != true) {
                    Utils.showConnectivityOffMessage(requireContext())
                    return@setOnClickListener
                }
                doBeforeRequest(binding)
                if (customer == null) viewModel.addToCart(sharedViewModel.token, 0, product.id)
                else viewModel.addToCart(sharedViewModel.token, customer!!.customerId, product.id)
            }
        }
    }

    private fun doBeforeRequest(binding: FragmentAddToCartBinding) {
        binding.apply {
            buttonAdd.isEnabled = false
            buttonAdd.text = ""
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun setEventListener(binding: FragmentAddToCartBinding) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addToCartEvent.collect { event ->
                when (event) {
                    is AddToCartViewModel.AddToCartEvent.OnResponse -> handleAddToCartResponse(
                        binding, event.message, event.cart)
                    is AddToCartViewModel.AddToCartEvent.OnFailure -> handleAddToCartRequestFailure(
                        binding)
                    is AddToCartViewModel.AddToCartEvent.UpdateQuantityEditText -> {
                        binding.editTextQuantity.setText(event.quantity)
                    }
                    is AddToCartViewModel.AddToCartEvent.ShowUnauthorizedDialog -> {
                        Utils.showUnauthorizedDialog(requireActivity())
                    }
                }
            }
        }
    }

    private fun handleAddToCartResponse(binding: FragmentAddToCartBinding, message: String,
        cart: Cart?) {
        doAfterResponse(binding)
        if (cart == null) {
            sharedViewModel.cartId.value = 0
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        } else {
            sharedViewModel.cartId.value = cart.id
            val action = if (customer == null) AddToCartFragmentCustomerDirections
                .actionAddToCartFragment2ToCartDetailsFragment2(
                    cart)
            else AddToCartFragmentSaleDirections.actionAddToCartFragmentToCartDetailsFragment(
                customer!!, cart)
            findNavController().navigate(action)
        }
    }

    private fun handleAddToCartRequestFailure(binding: FragmentAddToCartBinding) {
        doAfterResponse(binding)
        Utils.showConnectivityOffMessage(requireContext())
    }

    private fun doAfterResponse(binding: FragmentAddToCartBinding) {
        binding.apply {
            progressBar.visibility = View.GONE
            buttonAdd.text = getString(R.string.fragment_add_to_cart_button_add_text)
            buttonAdd.isEnabled = true
        }
    }
}
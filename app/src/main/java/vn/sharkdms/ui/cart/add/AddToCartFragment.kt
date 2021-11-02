package vn.sharkdms.ui.cart.add

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentAddToCartBinding
import vn.sharkdms.ui.cart.Cart
import vn.sharkdms.util.Formatter

@AndroidEntryPoint
class AddToCartFragment : Fragment(R.layout.fragment_add_to_cart) {

    private val args by navArgs<AddToCartFragmentArgs>()
    private val viewModel by viewModels<AddToCartViewModel>()
    private lateinit var sharedViewModel: SharedViewModel
    private var connectivity = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddToCartBinding.bind(view)
        bind(binding)
        setBackIconListener(binding)
        setMinusButtonListener(binding)
        setPlusButtonListener(binding)
        setAddButtonListener(binding)
        setEventListener(binding)
        viewModel.currentQuantity.observe(viewLifecycleOwner) { quantity ->
            binding.apply {
                textViewQuantity.text = quantity.toString()
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
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity = it }
    }

    private fun bind(binding: FragmentAddToCartBinding) {
        val product = args.product
        binding.apply {
            Glide.with(requireContext()).load(product.imageUrl).error(R.drawable.ic_product)
                .into(imageViewProduct)
            textViewName.text = product.name
            textViewAvailable.text = product.quantity.toString()
            textViewQuantity.text = "0"
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

    private fun setMinusButtonListener(binding: FragmentAddToCartBinding) {
        binding.buttonMinus.setOnClickListener {
            viewModel.minusOneToQuantity(args.product.price)
        }
    }

    private fun setPlusButtonListener(binding: FragmentAddToCartBinding) {
        binding.buttonPlus.setOnClickListener {
            val product = args.product
            viewModel.plusOneToQuantity(product.price, product.quantity)
        }
    }

    private fun setAddButtonListener(binding: FragmentAddToCartBinding) {
        binding.apply {
            buttonAdd.setOnClickListener {
                if (!connectivity) {
                    Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                doBeforeRequest(binding)
                viewModel.addToCart(sharedViewModel.token, args.customer.customerId,
                    args.product.id)
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
                }
            }
        }
    }

    private fun handleAddToCartResponse(binding: FragmentAddToCartBinding, message: String,
        cart: Cart?) {
        doAfterResponse(binding)
        if (cart == null) Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        else {
            val action = AddToCartFragmentDirections.actionAddToCartFragmentToCartDetailsFragment(
                args.customer, cart)
            findNavController().navigate(action)
        }
    }

    private fun handleAddToCartRequestFailure(binding: FragmentAddToCartBinding) {
        doAfterResponse(binding)
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }

    private fun doAfterResponse(binding: FragmentAddToCartBinding) {
        binding.apply {
            progressBar.visibility = View.GONE
            buttonAdd.text = getString(R.string.fragment_add_to_cart_button_add_text)
            buttonAdd.isEnabled = true
        }
    }
}
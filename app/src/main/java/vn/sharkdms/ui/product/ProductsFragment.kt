package vn.sharkdms.ui.product

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentProductsBinding
import vn.sharkdms.ui.customer.list.Customer
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.AdapterDataObserver
import vn.sharkdms.util.Utils

@AndroidEntryPoint
abstract class ProductsFragment : Fragment(
    R.layout.fragment_products), ProductAdapter.OnItemClickListener {

    private var customer: Customer? = null
    private val viewModel by viewModels<ProductsViewModel>()
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        customer = getCustomerFromArgs()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun getCustomerFromArgs(): Customer?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ProductAdapter(this)
        val binding = FragmentProductsBinding.bind(view)
        binding.apply {
            customer?.let { toolbar.visibility = View.VISIBLE }
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
        }
        adapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                val currentState = combinedLoadStates.source.refresh
                if (currentState is LoadState.Error) {
                    when (currentState.error) {
                        is UnauthorizedException -> Utils.showUnauthorizedDialog(requireActivity())
                        else -> Utils.showConnectivityOffMessage(requireContext())
                    }
                }
                if (combinedLoadStates.source.refresh is LoadState.NotLoading &&
                    combinedLoadStates.append.endOfPaginationReached && adapter.itemCount == 0) {
                    iconNoResult.visibility = View.VISIBLE
                    textViewNoResult.visibility = View.VISIBLE
                } else {
                    iconNoResult.visibility = View.GONE
                    textViewNoResult.visibility = View.GONE
                }
            }
        }
        adapter.registerAdapterDataObserver(AdapterDataObserver(binding.recyclerView))
        viewModel.tasks.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        setBackIconListener(binding)
        setNameEditTextListener(binding)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.searchProducts(sharedViewModel.token, "", customer)

        Utils.setupUI(binding.productsFragment, requireActivity() as AppCompatActivity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun setBackIconListener(binding: FragmentProductsBinding) {
        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNameEditTextListener(binding: FragmentProductsBinding) {
        binding.editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    viewModel.searchProducts(sharedViewModel.token, p0.toString(), customer)
                }
            }
        })
    }

    override fun onItemClick(product: Product) {
        val action = if (customer == null) ProductsFragmentCustomerDirections
            .actionProductsFragmentToAddToCartFragment2(
                product)
        else ProductsFragmentSaleDirections.actionProductsFragment2ToAddToCartFragment(product,
            customer)
        findNavController().navigate(action)
    }
}
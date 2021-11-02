package vn.sharkdms.ui.products

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentProductsBinding

@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products), ProductAdapter.OnItemClickListener {

    private val args by navArgs<ProductsFragmentArgs>()
    private val viewModel by viewModels<ProductsViewModel>()
    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ProductAdapter(this)
        val binding = FragmentProductsBinding.bind(view)
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        setBackIconListener(binding)
        setNameEditTextListener(binding)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.searchProducts(sharedViewModel.token, "")
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
                    viewModel.searchProducts(sharedViewModel.token, p0.toString())
                }
            }
        })
    }

    override fun onItemClick(product: Product) {
        val action = ProductsFragmentDirections.actionProductsFragment2ToAddToCartFragment(product,
            args.customer)
        findNavController().navigate(action)
    }
}
package vn.sharkdms.ui.customer

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentCustomerListBinding

import vn.sharkdms.util.HttpStatus
import androidx.appcompat.app.AppCompatActivity




@AndroidEntryPoint
class CustomerListFragment : Fragment(R.layout.fragment_customer_list) {

    private var TAG: String = "CustomerListFragment"
    private val viewModel by viewModels<CustomerListViewModel>()
    private lateinit var customerAdapter: CustomerAdapter

    companion object {
        private const val CHANGE_CUSTOMER = Activity.RESULT_FIRST_USER
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        val binding = FragmentCustomerListBinding.bind(view)
//        val customerListObserver = Observer<ArrayList<Customer>> {
//
//        }

        initRecyclerView(binding)
        initViewModel(binding)

//        updateListCustomer(page, "")
        setUsernameEditTextListener(binding)
        setBackButtonOnClickListener(binding)
        setAddCustomerButtonOnClickListener(binding)
//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            viewModel.customerListEvent.collectLatest { event ->
//                when (event) {
//                    is CustomerListViewModel.CustomerListEvent.OnResponse -> {
//                        handleCustomerListResponse(event.code, event.message)
//                    }
//                }
//            }
//        }
    }

    private fun initRecyclerView(binding: FragmentCustomerListBinding) {
        binding.apply {
            customerAdapter = CustomerAdapter()
            rvCustomer.adapter = customerAdapter
            rvCustomer.layoutManager = LinearLayoutManager(activity)
            val decoration = DividerItemDecoration(activity?.applicationContext, DividerItemDecoration.VERTICAL)
            rvCustomer.addItemDecoration(decoration)
        }
    }

    private fun initViewModel(binding: FragmentCustomerListBinding) {
        val viewModel = ViewModelProvider(this).get(CustomerListViewModel::class.java)
        lifecycleScope.launchWhenCreated {
            viewModel.getListData(binding.editTextCustomer.text.toString()).collectLatest {
                customerAdapter.submitData(it)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUsernameEditTextListener(binding: FragmentCustomerListBinding) {
        setCustomerEditTextTextChangedListener(binding)
    }

    private fun setCustomerEditTextTextChangedListener(binding: FragmentCustomerListBinding) {
        binding.editTextCustomer.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
//                updateListCustomer(page, p0.toString())
            }
        })
    }

    private fun updateListCustomer(page: Int, customerName: String) {
        viewModel.customerListRequest(page, customerName)
    }

    private fun setBackButtonOnClickListener(binding: FragmentCustomerListBinding) {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setAddCustomerButtonOnClickListener(binding: FragmentCustomerListBinding) {
        binding.ivAddCustomer.setOnClickListener {

        }
    }

//    private fun handleCustomerListResponse(code: Int, message: String) {
//        viewModel.getCustomerListDataObserver().observe(viewLifecycleOwner, Observer<CustomerList> {
//            if (it != null) {
//                viewModel.setAdapterData(it.customers)
//            } else {
//                Toast.makeText(activity, "Error in fetching data", Toast.LENGTH_LONG)
//            }
//        })
//        when (code) {
//            HttpStatus.OK -> Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
//            HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> Toast.makeText(requireContext(),
//                message, Toast.LENGTH_SHORT).show()
//            else -> Log.e(TAG, code.toString())
//        }
//    }
}
package vn.sharkdms.ui.customer

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
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
import kotlinx.coroutines.flow.collectLatest
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentCustomerListBinding

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.internal.Contexts


@AndroidEntryPoint
class CustomerListFragment : Fragment(R.layout.fragment_customer_list) {

    private var TAG: String = "CustomerListFragment"
    lateinit var viewModel: CustomerListViewModel
    private lateinit var customerAdapter: CustomerAdapter

    companion object {
        private const val CHANGE_CUSTOMER = Activity.RESULT_FIRST_USER
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        val binding = FragmentCustomerListBinding.bind(view)
        viewModel = ViewModelProvider(this).get(CustomerListViewModel::class.java)

        viewModel.customerList.observe(viewLifecycleOwner, Observer<ArrayList<Customer>> {
//            initViewModel(binding, binding.editTextCustomer.text.toString())
            if (it != null)
                viewModel.setAdapterData(it)
            else
                Toast.makeText(context, "Error in fetching data", Toast.LENGTH_LONG).show()
        })

        initRecyclerView(binding)
        initViewModel(binding, binding.editTextCustomer.text.toString())

        setCustomerEditTextListener(binding)
        setBackButtonOnClickListener(binding)
        setAddCustomerButtonOnClickListener(binding)
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

    private fun initViewModel(binding: FragmentCustomerListBinding, customerName: String) {
        lifecycleScope.launchWhenCreated {
            viewModel.getListData(customerName).collectLatest {
                customerAdapter.submitData(it)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCustomerEditTextListener(binding: FragmentCustomerListBinding) {
        setCustomerEditTextTextChangedListener(binding)
    }

    private fun setCustomerEditTextTextChangedListener(binding: FragmentCustomerListBinding) {
        binding.editTextCustomer.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                initViewModel(binding, binding.editTextCustomer.text.toString())
            }
        })
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

}
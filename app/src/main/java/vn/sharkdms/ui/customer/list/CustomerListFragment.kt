package vn.sharkdms.ui.customer.list

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentCustomerListBinding

import androidx.appcompat.content.res.AppCompatResources
import vn.sharkdms.SharedViewModel
import vn.sharkdms.util.Constant


class CustomerListFragment : Fragment(R.layout.fragment_customer_list) {

    private val TAG: String = "CustomerListFragment"
    lateinit var viewModel: CustomerListViewModel
    private lateinit var customerAdapter: CustomerAdapter
    private lateinit var sharedViewModel : SharedViewModel
    private lateinit var token: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCustomerListBinding.bind(view)
        val clearIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_clear)
        viewModel = ViewModelProvider(requireActivity())[CustomerListViewModel::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        token = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)

        viewModel.customerList.observe(viewLifecycleOwner, Observer<ArrayList<Customer>> {
            if (it != null) {
                viewModel.setAdapterData(it)
            } else {
                Toast.makeText(requireContext(), "Error in fetching data", Toast.LENGTH_LONG).show()
            }
        })

        initRecyclerView(binding)
        initViewModel(binding.editTextCustomer.text.toString())

        setCustomerEditTextListener(binding, clearIcon)
        setBackButtonOnClickListener(binding)
        setAddCustomerButtonOnClickListener(binding)
        setBtnGpsOnClickListener(binding)
    }

    private fun initRecyclerView(binding: FragmentCustomerListBinding) {
        binding.apply {
            customerAdapter = CustomerAdapter()
            rvCustomer.adapter = customerAdapter
            rvCustomer.layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initViewModel(customerName: String) {
        lifecycleScope.launchWhenCreated {
            viewModel.getListData(token, customerName).collectLatest {
                customerAdapter.submitData(it)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCustomerEditTextListener(binding: FragmentCustomerListBinding, clearIcon: Drawable?) {
        setCustomerEditTextTextChangedListener(binding)
        setCustomerEditTextOnTouchListener(binding, clearIcon)
        setCustomerEditTextFocusChangeListener(binding, clearIcon)
    }

    private fun setCustomerEditTextTextChangedListener(binding: FragmentCustomerListBinding) {
        binding.editTextCustomer.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding)
                initViewModel(binding.editTextCustomer.text.toString())
            }
        })
    }

    private fun setCustomerEditTextOnTouchListener(binding: FragmentCustomerListBinding, clearIcon: Drawable?) {
        binding.apply {
            editTextCustomer.apply {
                setOnTouchListener(object : View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        if (view == null || event == null || clearIcon == null || event.action !=
                            MotionEvent.ACTION_UP) return false
                        val currentClearIcon = editTextCustomer.compoundDrawablesRelative[2]
                        if (event.rawX < editTextCustomer.right - clearIcon.bounds.width() -
                            editTextCustomer.paddingEnd * 2 || currentClearIcon == null) {
                            return false
                        }
                        editTextCustomer.text.clear()
                        afterTextChanged(binding)
                        return true
                    }
                })
            }
        }
    }

    private fun setCustomerEditTextFocusChangeListener(binding: FragmentCustomerListBinding, clearIcon: Drawable?) {
        binding.apply {
            editTextCustomer.apply {
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    val currentUsernameIcon = editTextCustomer.compoundDrawablesRelative[0]
                    editTextCustomer.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        currentUsernameIcon, null,
                        if (hasFocus && editTextCustomer.text.toString().isNotEmpty()) clearIcon
                        else null, null)
                }
            }
        }
    }

    private fun afterTextChanged(binding: FragmentCustomerListBinding) {
        val customer = binding.editTextCustomer.text.toString()
        val usernameIcon = R.drawable.ic_username_invalid
        val clearIcon = if (customer.isNotEmpty() && binding.editTextCustomer.hasFocus()) R.drawable
            .ic_clear else 0
        binding.editTextCustomer.setCompoundDrawablesRelativeWithIntrinsicBounds(usernameIcon, 0, clearIcon,
            0)
    }

    private fun setBackButtonOnClickListener(binding: FragmentCustomerListBinding) {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setAddCustomerButtonOnClickListener(binding: FragmentCustomerListBinding) {
        binding.ivAddCustomer.setOnClickListener {
            val action = CustomerListFragmentDirections.actionCustomerListFragmentToCreateCustomerFragment()
            findNavController().navigate(action)
        }
    }

    private fun setBtnGpsOnClickListener(binding: FragmentCustomerListBinding) {
        binding.fabGps.setOnClickListener {
            val action = CustomerListFragmentDirections.actionCustomerListFragmentToMapsFragment()
            findNavController().navigate(action)
        }
    }

}
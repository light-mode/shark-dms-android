package vn.sharkdms.ui.customer.list

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import vn.sharkdms.SaleActivity
import vn.sharkdms.SharedViewModel
import vn.sharkdms.util.Constant


open class CustomerListFragment : Fragment(R.layout.fragment_customer_list), CustomerAdapter.OnItemClickListener {

    private val TAG: String = "CustomerListFragment"
    lateinit var viewModel: CustomerListViewModel
    private lateinit var customerAdapter: CustomerAdapter
    private lateinit var sharedViewModel : SharedViewModel
    private lateinit var token: String
    private lateinit var customers: ArrayList<Customer>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCustomerListBinding.bind(view)
        val clearIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_clear)
        viewModel = ViewModelProvider(requireActivity())[CustomerListViewModel::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        token = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)

        initRecyclerView(binding)
        initViewModel(binding.editTextCustomer.text.toString())

        viewModel.customers.observe(viewLifecycleOwner) {
            customerAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        customerAdapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                if (customerAdapter.itemCount == 0) {
                    ivNoCustomer.visibility = View.VISIBLE
                    tvNoCustomer.visibility = View.VISIBLE
                } else {
                    ivNoCustomer.visibility = View.GONE
                    tvNoCustomer.visibility = View.GONE
                }
            }
        }
        customers = customerAdapter.getDataList()

        setCustomerEditTextListener(binding, clearIcon)
        setAddCustomerButtonOnClickListener(binding)
        setBtnGpsOnClickListener(binding)

        Constant.setupUI(binding.customerListFragment, requireActivity() as AppCompatActivity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun initRecyclerView(binding: FragmentCustomerListBinding) {
        binding.apply {
            iconMenu.setOnClickListener {
                (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
            }
            customerAdapter = CustomerAdapter(this@CustomerListFragment)
            rvCustomer.adapter = customerAdapter
            rvCustomer.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initViewModel(customerName: String) {
        viewModel.searchCustomer(token, customerName)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCustomerEditTextListener(binding: FragmentCustomerListBinding, clearIcon: Drawable?) {
        setCustomerEditTextTextChangedListener(binding)
        setCustomerEditTextOnTouchListener(binding, clearIcon)
        setCustomerEditTextFocusChangeListener(binding, clearIcon)
    }

    private fun setCustomerEditTextTextChangedListener(binding: FragmentCustomerListBinding) {
        binding.apply {
            editTextCustomer.addTextChangedListener(object: TextWatcher {
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

    private fun setAddCustomerButtonOnClickListener(binding: FragmentCustomerListBinding) {
        binding.ivAddCustomer.setOnClickListener {
            val action = CustomerListFragmentDirections.actionCustomerListFragmentToCreateCustomerFragment()
            findNavController().navigate(action)
        }
    }

    private fun setBtnGpsOnClickListener(binding: FragmentCustomerListBinding) {
        binding.fabGps.setOnClickListener {
            val action = CustomerListFragmentDirections.actionCustomerListFragmentToMapsFragment(customers.toTypedArray())
            findNavController().navigate(action)
        }
    }

    override fun onItemClick(customer: Customer) {
        val action = CustomerListFragmentDirections.actionCustomerListFragmentToCustomerInfoFragment(customer)
        findNavController().navigate(action)
    }

}
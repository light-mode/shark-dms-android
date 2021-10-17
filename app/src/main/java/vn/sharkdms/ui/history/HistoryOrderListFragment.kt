package vn.sharkdms.ui.history

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentHistoryOrderListBinding
import vn.sharkdms.util.Constant

class HistoryOrderListFragment : Fragment(R.layout.fragment_history_order_list) {

    private val TAG = "HistoryOrderListFragment"
    lateinit var viewModel: HistoryOrderListViewModel
    private lateinit var historyOrderAdapter: HistoryOrderAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var token: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHistoryOrderListBinding.bind(view)
        val clearIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_clear)
        viewModel = ViewModelProvider(requireActivity())[HistoryOrderListViewModel::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        token = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)

        viewModel.historyOrderList.observe(viewLifecycleOwner, Observer<ArrayList<HistoryOrder>> {
            if (it != null)
                viewModel.setAdapterData(it)
            else
                Toast.makeText(requireContext(), "Error in fetching data", Toast.LENGTH_LONG).show()
        })

        initRecyclerView(binding)
        initViewModel(binding.etSearchOrder.text.toString(), binding.tvDatePicker.text.toString())
        setCustomerEditTextListener(binding, clearIcon)
        setBackButtonOnClickListener(binding)
        setTvDatePickerOnClickListener(binding)
    }

    private fun initRecyclerView(binding: FragmentHistoryOrderListBinding) {
        binding.apply {
            historyOrderAdapter = HistoryOrderAdapter()
            rvHistoryOrder.adapter = historyOrderAdapter
            rvHistoryOrder.layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initViewModel(customerName: String, date: String) {
        lifecycleScope.launchWhenCreated {
            viewModel.getListData(token, customerName, date).collectLatest {
                historyOrderAdapter.submitData(it)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCustomerEditTextListener(binding: FragmentHistoryOrderListBinding, clearIcon: Drawable?) {
        setCustomerEditTextTextChangedListener(binding)
        setCustomerEditTextOnTouchListener(binding, clearIcon)
        setCustomerEditTextFocusChangeListener(binding, clearIcon)
    }

    private fun setCustomerEditTextTextChangedListener(binding: FragmentHistoryOrderListBinding) {
        binding.etSearchOrder.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding)
                initViewModel(binding.etSearchOrder.text.toString(), binding.tvDatePicker.text.toString())
            }
        })
    }

    private fun setCustomerEditTextOnTouchListener(binding: FragmentHistoryOrderListBinding, clearIcon: Drawable?) {
        binding.apply {
            etSearchOrder.apply {
                setOnTouchListener(object : View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        if (view == null || event == null || clearIcon == null || event.action !=
                            MotionEvent.ACTION_UP) return false
                        val currentClearIcon = etSearchOrder.compoundDrawablesRelative[2]
                        if (event.rawX < etSearchOrder.right - clearIcon.bounds.width() -
                            etSearchOrder.paddingEnd * 2 || currentClearIcon == null) {
                            return false
                        }
                        etSearchOrder.text.clear()
                        afterTextChanged(binding)
                        return true
                    }
                })
            }
        }
    }

    private fun setCustomerEditTextFocusChangeListener(binding: FragmentHistoryOrderListBinding, clearIcon: Drawable?) {
        binding.apply {
            etSearchOrder.apply {
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    val currentUsernameIcon = etSearchOrder.compoundDrawablesRelative[0]
                    etSearchOrder.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        currentUsernameIcon, null,
                        if (hasFocus && etSearchOrder.text.toString().isNotEmpty()) clearIcon
                        else null, null)
                }
            }
        }
    }

    private fun afterTextChanged(binding: FragmentHistoryOrderListBinding) {
        val customer = binding.etSearchOrder.text.toString()
        val usernameIcon = R.drawable.ic_username_invalid
        val clearIcon = if (customer.isNotEmpty() && binding.etSearchOrder.hasFocus()) R.drawable
            .ic_clear else 0
        binding.etSearchOrder.setCompoundDrawablesRelativeWithIntrinsicBounds(usernameIcon, 0, clearIcon,
            0)
    }

    private fun setBackButtonOnClickListener(binding: FragmentHistoryOrderListBinding) {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setTvDatePickerOnClickListener(binding: FragmentHistoryOrderListBinding) {
        binding.tvDatePicker.setOnClickListener {

        }
    }
}
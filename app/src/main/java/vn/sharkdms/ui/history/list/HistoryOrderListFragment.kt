package vn.sharkdms.ui.history.list

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import vn.sharkdms.R
import vn.sharkdms.activity.SaleActivity
import vn.sharkdms.activity.SharedViewModel
import vn.sharkdms.databinding.FragmentHistoryOrderListBinding
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.AdapterDataObserver
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Utils
import java.util.*

open class HistoryOrderListFragment : Fragment(R.layout.fragment_history_order_list),
    HistoryOrderAdapter.OnItemClickListener {

    open lateinit var viewModelSale: HistoryOrderListViewModelSale
    open lateinit var historyOrderAdapter: HistoryOrderAdapter
    open lateinit var sharedViewModel: SharedViewModel
    open lateinit var token: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHistoryOrderListBinding.bind(view)
        val clearIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_clear)
        viewModelSale = ViewModelProvider(requireActivity())[HistoryOrderListViewModelSale::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        token = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)

        initRecyclerView(binding)

        historyOrderAdapter.addLoadStateListener { combinedLoadStates ->
            val currentState = combinedLoadStates.source.refresh
            if (currentState is LoadState.Error) {
                when (currentState.error) {
                    is UnauthorizedException -> Utils.showUnauthorizedDialog(requireActivity())
                    else -> Utils.showConnectivityOffMessage(requireContext())
                }
            }
            binding.apply {
                if (historyOrderAdapter.itemCount == 0) {
                    ivNoOrder.visibility = View.VISIBLE
                    tvNoOrder.visibility = View.VISIBLE
                } else {
                    ivNoOrder.visibility = View.GONE
                    tvNoOrder.visibility = View.GONE
                }
            }
        }
        historyOrderAdapter.registerAdapterDataObserver(AdapterDataObserver(binding.rvHistoryOrder))
        setCustomerEditTextListener(binding, clearIcon)
        setTvDatePickerListener(binding, clearIcon)

        Utils.setupUI(binding.historyOrderListFragment, requireActivity() as AppCompatActivity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun initRecyclerView(binding: FragmentHistoryOrderListBinding) {
        binding.apply {
            iconMenu.setOnClickListener {
                (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
            }
            historyOrderAdapter = HistoryOrderAdapter(this@HistoryOrderListFragment)
            rvHistoryOrder.adapter = historyOrderAdapter
            rvHistoryOrder.layoutManager = LinearLayoutManager(activity)
        }
    }

    open fun initViewModel(customerName: String, date: String) {
        viewModelSale.getListData(token, customerName, date).observe(viewLifecycleOwner) {
            historyOrderAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTvDatePickerListener(binding: FragmentHistoryOrderListBinding, clearIcon: Drawable?) {
        setTvDatePickerOnClickListener(binding, clearIcon)
        setTvDatePickerOnTouchListener(binding, clearIcon)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCustomerEditTextListener(binding: FragmentHistoryOrderListBinding, clearIcon: Drawable?) {
        setCustomerEditTextTextChangedListener(binding)
        setCustomerEditTextOnTouchListener(binding, clearIcon)
        setCustomerEditTextFocusChangeListener(binding, clearIcon)
    }

    open fun setCustomerEditTextTextChangedListener(binding: FragmentHistoryOrderListBinding) {
        binding.etSearchOrder.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding)
                if(binding.tvDatePicker.text != "Chọn ngày") {
                    val dateNum = binding.tvDatePicker.text.toString().split("/").toTypedArray()
                    val date = dateNum.joinToString("-")
                    initViewModel(binding.etSearchOrder.text.toString(), date)
                }
                else
                    initViewModel(binding.etSearchOrder.text.toString(), "")
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

    @SuppressLint("SetTextI18n")
    open fun setTvDatePickerOnClickListener(binding: FragmentHistoryOrderListBinding, clearIcon: Drawable?) {
        binding.apply {
            tvDatePicker.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year: Int
                val month: Int
                val day: Int
                if (tvDatePicker.text == getString(R.string.fragment_history_date_picker_sample)) {
                    year = calendar.get(Calendar.YEAR)
                    month = calendar.get(Calendar.MONTH)
                    day = calendar.get(Calendar.DAY_OF_MONTH)
                } else {
                    val datenum = tvDatePicker.text.toString().split("/").toTypedArray()
                    year = datenum[2].toInt()
                    month = datenum[1].toInt() - 1
                    day = datenum[0].toInt()
                }
                val dpd = DatePickerDialog(requireActivity(), R.style.date_picker_dialog,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        tvDatePicker.text = selectedDay.toString() + "/" + (selectedMonth + 1).toString() + "/" + selectedYear.toString()
                        initViewModel(etSearchOrder.text.toString(),
                            date = selectedDay.toString() + "-" + (selectedMonth + 1).toString() + "-" + selectedYear.toString())
                        tvDatePickerDateChangeListener(binding, clearIcon)
                    }, year, month, day)
                dpd.show()
                val primaryColor = Color.parseColor(getString(R.string.color_primary))
                dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(primaryColor)
                dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(primaryColor)
            }
        }
    }

    open fun setTvDatePickerOnTouchListener(binding: FragmentHistoryOrderListBinding, clearIcon: Drawable?) {
        binding.apply {
            tvDatePicker.apply {
                setOnTouchListener(object : View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        if (view == null || event == null || clearIcon == null || event.action !=
                            MotionEvent.ACTION_UP) return false
                        val currentClearIcon = tvDatePicker.compoundDrawablesRelative[2]
                        if (event.rawX < tvDatePicker.right - clearIcon.bounds.width() -
                            tvDatePicker.paddingEnd * 2 || currentClearIcon == null) {
                            return false
                        }
                        tvDatePicker.text = getString(R.string.fragment_history_date_picker_sample)
                        val dateIcon = R.drawable.ic_date_picker
                        binding.tvDatePicker.setCompoundDrawablesRelativeWithIntrinsicBounds(dateIcon, 0, 0,
                            0)
                        initViewModel(etSearchOrder.text.toString(), "")
                        return true
                    }
                })
            }
        }
    }

    private fun tvDatePickerDateChangeListener(binding: FragmentHistoryOrderListBinding, clearIcon: Drawable?) {
        binding.apply {
            val datePickerText = tvDatePicker.text.toString()
            val currentUsernameIcon = tvDatePicker.compoundDrawablesRelative[0]
            tvDatePicker.setCompoundDrawablesRelativeWithIntrinsicBounds(
                currentUsernameIcon, null,
                if (datePickerText != getString(R.string.fragment_history_date_picker_sample)) clearIcon
                else null, null)
        }
    }

    override fun onItemClick(historyOrder: HistoryOrder) { //
    }
}
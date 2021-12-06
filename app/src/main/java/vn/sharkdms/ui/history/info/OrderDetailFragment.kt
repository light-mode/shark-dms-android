package vn.sharkdms.ui.history.info

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.api.OrderDetailRequest
import vn.sharkdms.databinding.FragmentOrderDetailBinding
import vn.sharkdms.ui.base.history.info.OrderItem
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Formatter
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.Utils

open class OrderDetailFragment :Fragment(R.layout.fragment_order_detail) {

    companion object {
        private const val TAG: String = "OrderDetailFragment"
    }

    lateinit var viewModel: OrderDetailViewModelSale
    open lateinit var orderItemAdapter: OrderItemAdapter
    open lateinit var sharedViewModel : SharedViewModel
    open lateinit var token: String

    open var orderItems: List<OrderItem>? = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentOrderDetailBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[OrderDetailViewModelSale::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        token = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.orderDetailEvent.collect { event ->
                when(event) {
                    is OrderDetailViewModelSale.OrderDetailEvent.OnResponse ->
                        handleOrderDetailResponse(binding, event.code, event.message, event.data)
                    is OrderDetailViewModelSale.OrderDetailEvent.OnFailure ->
                        Utils.showConnectivityOffMessage(requireContext())
                    is OrderDetailViewModelSale.OrderDetailEvent.ShowUnauthorizedDialog ->
                        Utils.showUnauthorizedDialog(requireActivity())
                }
            }
        }
        setBtnBackOnClickListener(binding)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun initRecyclerView(binding: FragmentOrderDetailBinding) {
        binding.apply {
            orderItemAdapter = OrderItemAdapter(orderItems)
            rvProductItem.adapter = orderItemAdapter
            rvProductItem.layoutManager = LinearLayoutManager(activity)
        }
    }

    open fun initViewModel(orderId: String) {
        val orderDetailRequest = OrderDetailRequest(orderId)
        viewModel.getOrderDetail(token, orderDetailRequest)
    }

    @SuppressLint("SetTextI18n")
    open fun handleOrderDetailResponse(binding: FragmentOrderDetailBinding, code: Int, message: String, data: OrderDetail?) {
        binding.apply {
            when(code) {
                HttpStatus.OK -> {
                    tvCustomerName.text = data?.customerName.toString()
                    tvCustomerPhone.text = data?.customerPhone.toString()
                    tvCustomerOrderDetailNum.text =
                        getString(R.string.fragment_order_detail_num_format, data?.orderItems?.size)
                    tvOrderDiscountSample.text = Formatter.formatCurrency(data?.discount.toString())
                        .plus(" ").plus(data?.orderItems?.get(0)?.currency)
                    tvOrderTotalAmountSample.text = Formatter.formatCurrency(data?.totalAmount.toString())
                        .plus(" ").plus(data?.orderItems?.get(0)?.currency)
                    tvOrderNoteInput.text = data?.note
                    when(data?.status) {
                        Constant.ORDER_STATUS_NEW -> {
                            tvCustomerOrderStatusDetail.text = Constant.ORDER_STATUS_NEW
                            tvCustomerOrderStatusDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_order_status_new, 0, 0, 0)
                        }
                        Constant.ORDER_STATUS_PROCESSING_QUERY -> {
                            tvCustomerOrderStatusDetail.text = Constant.ORDER_STATUS_PROCESSING
                            tvCustomerOrderStatusDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_order_status_processing, 0, 0, 0)
                        }
                        Constant.ORDER_STATUS_DONE -> {
                            tvCustomerOrderStatusDetail.text = Constant.ORDER_STATUS_DONE
                            tvCustomerOrderStatusDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_order_status_done, 0, 0, 0)
                        }
                        Constant.ORDER_STATUS_CANCEL_QUERY -> {
                            tvCustomerOrderStatusDetail.text = Constant.ORDER_STATUS_CANCEL
                            tvCustomerOrderStatusDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_order_status_cancel, 0, 0, 0)
                        }
                        Constant.ORDER_STATUS_STOCK_OUT -> {
                            tvCustomerOrderStatusDetail.text = Constant.ORDER_STATUS_STOCK_OUT
                            tvCustomerOrderStatusDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_order_status_stockout, 0, 0, 0)
                        }
                    }
                    orderItems = data?.orderItems
                    initRecyclerView(binding)
                }
                HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> Toast.makeText(requireContext(),
                    message, Toast.LENGTH_SHORT).show()
                else -> Log.e(TAG, code.toString())
            }
        }
    }

    private fun setBtnBackOnClickListener(binding: FragmentOrderDetailBinding) {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
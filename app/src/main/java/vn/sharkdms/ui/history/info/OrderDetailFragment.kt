package vn.sharkdms.ui.history.info

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.api.OrderDetailRequest
import vn.sharkdms.databinding.FragmentOrderDetailBinding
import vn.sharkdms.ui.customer.create.CreateCustomerFragment
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus

class OrderDetailFragment :Fragment(R.layout.fragment_order_detail) {

    private val TAG: String = "OrderDetailFragment"
    lateinit var viewModel: OrderDetailViewModel
    private lateinit var orderItemAdapter: OrderItemAdapter
    private lateinit var sharedViewModel : SharedViewModel
    private lateinit var token: String

    private val args by navArgs<OrderDetailFragmentArgs>()
    private var orderItems: List<OrderItem>? = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentOrderDetailBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[OrderDetailViewModel::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        token = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)

        initViewModel(args.order.orderId.toString())
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.orderDetailEvent.collect { event ->
                when(event) {
                    is OrderDetailViewModel.OrderDetailEvent.OnResponse ->
                        handleOrderDetailResponse(binding, event.code, event.message, event.data)
                    is OrderDetailViewModel.OrderDetailEvent.OnFailure ->
                        handleOrderDetailFailure()
                }
            }
        }
        setBtnBackOnClickListener(binding)
    }

    private fun initRecyclerView(binding: FragmentOrderDetailBinding) {
        binding.apply {
            orderItemAdapter = OrderItemAdapter(orderItems)
            rvProductItem.adapter = orderItemAdapter
            rvProductItem.layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initViewModel(orderId: String) {
        val orderDetailRequest = OrderDetailRequest(orderId)
        viewModel.getOrderDetail(token, orderDetailRequest)
    }

    @SuppressLint("SetTextI18n")
    private fun handleOrderDetailResponse(binding: FragmentOrderDetailBinding, code: Int, message: String, data: OrderDetail?) {
        binding.apply {
            when(code) {
                HttpStatus.OK -> {
                    tvCustomerName.text = data?.customerName.toString()
                    tvCustomerPhone.text = data?.customerPhone.toString()
                    tvCustomerOrderDetailNum.text = data?.orderItems?.size.toString() + Constant.ORDER_PRODUCT_AMOUNT
                    tvOrderDiscountSample.text = data?.discount.toString().plus(" ")
                        .plus(data?.orderItems?.get(0)?.currency)
                    tvOrderTotalAmountSample.text = data?.totalAmount.toString().plus(" ")
                        .plus(data?.orderItems?.get(0)?.currency)
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
                        Constant.ORDER_STATUS_CANCEL -> {
                            tvCustomerOrderStatusDetail.text = Constant.ORDER_STATUS_CANCEL
                            tvCustomerOrderStatusDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_order_status_cancel, 0, 0, 0)
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

    private fun handleOrderDetailFailure() {
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }

    private fun setBtnBackOnClickListener(binding: FragmentOrderDetailBinding) {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
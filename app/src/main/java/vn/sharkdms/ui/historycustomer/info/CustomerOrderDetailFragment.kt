package vn.sharkdms.ui.historycustomer.info

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import vn.sharkdms.databinding.FragmentOrderDetailBinding
import vn.sharkdms.ui.base.history.info.BaseOrderDetailFragment

class CustomerOrderDetailFragment : BaseOrderDetailFragment() {
    lateinit var customerViewModel: CustomerOrderDetailViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOrderDetailBinding.bind(view)
        customerViewModel = ViewModelProvider(requireActivity())[CustomerOrderDetailViewModel::class.java]

        initViewModel(args.order.orderId.toString())
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            customerViewModel.orderDetailEvent.collect { event ->
                when(event) {
                    is CustomerOrderDetailViewModel.OrderDetailEvent.OnResponse ->
                        handleOrderDetailResponse(binding, event.code, event.message, event.data)
                    is CustomerOrderDetailViewModel.OrderDetailEvent.OnFailure ->
                        handleOrderDetailFailure()
                }
            }
        }
    }

    override fun initViewModel(orderId: String) {
        customerViewModel.getOrderDetail(token, orderId.toInt())
    }
}
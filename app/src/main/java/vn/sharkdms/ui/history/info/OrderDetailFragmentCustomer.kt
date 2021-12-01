package vn.sharkdms.ui.history.info

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collect
import vn.sharkdms.databinding.FragmentOrderDetailBinding

class OrderDetailFragmentCustomer : OrderDetailFragment() {
    lateinit var viewModelCustomer: OrderDetailViewModelCustomer
    private val args by navArgs<OrderDetailFragmentCustomerArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOrderDetailBinding.bind(view)
        viewModelCustomer = ViewModelProvider(requireActivity())[OrderDetailViewModelCustomer::class.java]

        initViewModel(args.order.orderId.toString())
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModelCustomer.orderDetailEvent.collect { event ->
                when(event) {
                    is OrderDetailViewModelCustomer.OrderDetailEvent.OnResponse ->
                        handleOrderDetailResponse(binding, event.code, event.message, event.data)
                    is OrderDetailViewModelCustomer.OrderDetailEvent.OnFailure ->
                        handleOrderDetailFailure()
                }
            }
        }
    }

    override fun initViewModel(orderId: String) {
        viewModelCustomer.getOrderDetail(token, orderId.toInt())
    }
}
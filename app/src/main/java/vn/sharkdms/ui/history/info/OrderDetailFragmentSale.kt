package vn.sharkdms.ui.history.info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs

class OrderDetailFragmentSale : OrderDetailFragment() {
    private val args by navArgs<OrderDetailFragmentSaleArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(args.order.orderId.toString())
    }
}
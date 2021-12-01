package vn.sharkdms.ui.history.info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import vn.sharkdms.databinding.FragmentOrderDetailBinding

class OrderDetailFragmentSale : OrderDetailFragment() {
    private val args by navArgs<OrderDetailFragmentSaleArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOrderDetailBinding.bind(view)
        initViewModel(args.order.orderId.toString())
    }
}
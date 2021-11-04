package vn.sharkdms.ui.history.info

import android.os.Bundle
import android.view.View
import vn.sharkdms.databinding.FragmentOrderDetailBinding
import vn.sharkdms.ui.base.history.info.BaseOrderDetailFragment

class OrderDetailFragment : BaseOrderDetailFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOrderDetailBinding.bind(view)
        initViewModel(args.order.orderId.toString())
    }
}
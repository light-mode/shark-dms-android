package vn.sharkdms.ui.history.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.api.HistoryOrder
import vn.sharkdms.databinding.FragmentHistoryOrderListBinding

@AndroidEntryPoint
class HistoryOrderListFragmentSale: HistoryOrderListFragment(), HistoryOrderAdapter.OnItemClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHistoryOrderListBinding.bind(view)
        initViewModel(binding.etSearchOrder.text.toString(), "")
    }

    override fun onItemClick(historyOrder: HistoryOrder) {
        val action = HistoryOrderListFragmentSaleDirections.actionHistoryOrderListFragmentToOrderInfoFragment(historyOrder)
        findNavController().navigate(action)
    }
}
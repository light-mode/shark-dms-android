package vn.sharkdms.ui.history.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentHistoryOrderListBinding
import vn.sharkdms.ui.base.history.list.BaseHistoryOrderListFragment
import vn.sharkdms.ui.base.history.list.HistoryOrder
import vn.sharkdms.ui.base.history.list.HistoryOrderAdapter
import vn.sharkdms.ui.base.history.list.HistoryOrderListViewModel
import vn.sharkdms.util.Constant

class HistoryOrderListFragment: BaseHistoryOrderListFragment(), HistoryOrderAdapter.OnItemClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHistoryOrderListBinding.bind(view)
        initViewModel(binding.etSearchOrder.text.toString(), "")
    }

    override fun onItemClick(historyOrder: HistoryOrder) {
        val action = HistoryOrderListFragmentDirections.actionHistoryOrderListFragmentToOrderInfoFragment(historyOrder)
        findNavController().navigate(action)
    }
}
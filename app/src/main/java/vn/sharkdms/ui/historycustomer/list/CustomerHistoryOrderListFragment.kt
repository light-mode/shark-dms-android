package vn.sharkdms.ui.historycustomer.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import vn.sharkdms.databinding.FragmentHistoryOrderListBinding
import vn.sharkdms.ui.base.history.list.BaseHistoryOrderListFragment
import vn.sharkdms.ui.base.history.list.HistoryOrder
import vn.sharkdms.ui.base.history.list.HistoryOrderAdapter

@AndroidEntryPoint
class CustomerHistoryOrderListFragment: BaseHistoryOrderListFragment(), HistoryOrderAdapter.OnItemClickListener {
    lateinit var customerViewModel: CustomerHistoryOrderListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHistoryOrderListBinding.bind(view)
        customerViewModel = ViewModelProvider(requireActivity())[CustomerHistoryOrderListViewModel::class.java]
        initViewModel("", "")

//        customerViewModel.historyOrderList.observe(viewLifecycleOwner) {
//            customerViewModel.setAdapterData(it)
//        }

        binding.toolbarHeader.visibility = View.GONE
    }

    override fun initViewModel(customerName: String, date: String) {
//        lifecycleScope.launchWhenCreated {
//            customerViewModel.getListData(token, date).collectLatest {
//                historyOrderAdapter.submitData(it)
//            }
//        }
        viewModel.getListData(token, customerName, date).observe(viewLifecycleOwner) {
            historyOrderAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onItemClick(historyOrder: HistoryOrder) {
        val action = CustomerHistoryOrderListFragmentDirections
            .actionCustomerHistoryOrderListFragmentToBaseOrderDetailFragment(historyOrder)
        findNavController().navigate(action)
    }
}
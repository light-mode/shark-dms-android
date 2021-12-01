package vn.sharkdms.ui.history.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.databinding.FragmentHistoryOrderListBinding

@AndroidEntryPoint
class HistoryOrderListFragmentCustomer: HistoryOrderListFragment(), HistoryOrderAdapter.OnItemClickListener {
    lateinit var viewModelCustomer: HistoryOrderListViewModelCustomer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHistoryOrderListBinding.bind(view)
        viewModelCustomer = ViewModelProvider(requireActivity())[HistoryOrderListViewModelCustomer::class.java]
        initViewModel("", "")

        binding.toolbarHeader.visibility = View.GONE
    }

    override fun initViewModel(customerName: String, date: String) {
        viewModelCustomer.getListData(token, date).observe(viewLifecycleOwner) {
            historyOrderAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onItemClick(historyOrder: HistoryOrder) {
        val action = HistoryOrderListFragmentCustomerDirections
            .actionCustomerHistoryOrderListFragmentToBaseOrderDetailFragment(historyOrder)
        findNavController().navigate(action)
    }
}
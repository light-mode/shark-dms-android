package vn.sharkdms.ui.history.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentOrderDetailBinding
import vn.sharkdms.util.Constant

class OrderDetailFragment :Fragment(R.layout.fragment_order_detail) {

    private val TAG: String = "OrderDetailFragment"
    lateinit var viewModel: OrderDetailViewModel
    private lateinit var orderItemAdapter: OrderItemAdapter
    private lateinit var sharedViewModel : SharedViewModel
    private lateinit var token: String

    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentOrderDetailBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[OrderDetailViewModel::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        token = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)

        initRecyclerView(binding)
        initViewModel(args.order.orderId)
    }

    private fun initRecyclerView(binding: FragmentOrderDetailBinding) {
        binding.apply {
            orderItemAdapter = OrderItemAdapter(mutableListOf())
            rvProductItem.adapter = orderItemAdapter
            rvProductItem.layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initViewModel(orderId: Int) {
        lifecycleScope.launchWhenCreated {
            viewModel.getOrderDetail(token, orderId)
        }
    }
}
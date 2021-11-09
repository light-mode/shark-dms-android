package vn.sharkdms.ui.cart.result

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentOrderResultBinding
import vn.sharkdms.ui.cart.CartItem
import vn.sharkdms.ui.cart.details.CartItemAdapter
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Formatter

@AndroidEntryPoint
class OrderResultFragment : Fragment(
    R.layout.fragment_order_result), CartItemAdapter.OnItemClickListener {

    private val args by navArgs<OrderResultFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOrderResultBinding.bind(view)
        bind(binding)
        setListener(binding)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun bind(binding: FragmentOrderResultBinding) {
        bindOrderInfo(binding)
        bindOrderItem(binding)
    }

    private fun bindOrderInfo(binding: FragmentOrderResultBinding) {
        val data = args.data
        binding.apply {
            textViewOrderCode.text = data.orderCode
            textViewCustomerName.text = data.customerName
            textViewDiscount.text = getString(R.string.fragment_order_result_currency_format,
                Formatter.formatCurrency(data.discount))
            var totalAmount = data.totalAmount
            if (totalAmount < 0) totalAmount = 0
            textViewTotalAmount.text = getString(R.string.fragment_order_result_currency_format,
                Formatter.formatCurrency(totalAmount.toString()))
            textViewNote.text = data.note
            textViewOrderTimestamp.text = data.orderTimestamp
        }
    }

    private fun bindOrderItem(binding: FragmentOrderResultBinding) {
        val adapter = CartItemAdapter(args.data.items, this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setAdapter(adapter)
        }
    }

    private fun setListener(binding: FragmentOrderResultBinding) {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onRemoveItemClick(item: CartItem) { //
    }
}
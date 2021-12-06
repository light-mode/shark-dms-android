package vn.sharkdms.ui.history.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.sharkdms.R
import vn.sharkdms.databinding.ItemOrderProductBinding
import vn.sharkdms.ui.base.history.info.OrderItem
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Formatter

class OrderItemAdapter(
    private val orderItems: List<OrderItem>?
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(private val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: OrderItem?) {
            binding.apply {
                tvProductName.text =
                    Formatter.collapseDisplay(data?.productName as String, Constant.PRODUCT_LIMIT)
                tvProductQuantity.text = data.qty.toString()
                tvProductTotal.text = itemView.context.getString(
                    R.string.fragment_order_product_item_total_format,
                    Formatter.formatCurrency(data.totalPrice.toString()),
                    data.currency
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return OrderItemViewHolder(ItemOrderProductBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(orderItems?.get(position))
    }

    override fun getItemCount(): Int {
        return orderItems?.size ?: 0
    }
}
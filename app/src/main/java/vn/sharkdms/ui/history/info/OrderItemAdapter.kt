package vn.sharkdms.ui.history.info

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.sharkdms.R

class OrderItemAdapter(
    private val orderItems: List<OrderItem>?
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductname = itemView.findViewById<TextView>(R.id.tv_product_name)
        val tvProductQuantity = itemView.findViewById<TextView>(R.id.tv_product_quantity)
        val tvProductTotal = itemView.findViewById<TextView>(R.id.tv_product_total)

        @SuppressLint("SetTextI18n")
        fun bind(data: OrderItem?) {
            tvProductname.text = data?.productName
            tvProductQuantity.text = data?.qty.toString()
            tvProductTotal.text = data?.totalPrice.toString() + data?.currency
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return  OrderItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_order_product,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(orderItems?.get(position))
    }

    override fun getItemCount(): Int {
        return orderItems?.size ?: 0
    }
}
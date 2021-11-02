package vn.sharkdms.ui.cart.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.sharkdms.R
import vn.sharkdms.databinding.ItemCartItemBinding
import vn.sharkdms.ui.cart.CartItem
import vn.sharkdms.ui.cart.result.OrderResultFragment
import vn.sharkdms.util.Formatter

class CartItemAdapter(private val items: List<CartItem>,
    private val listener: OnItemClickListener) : RecyclerView.Adapter<CartItemAdapter
.CartItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CartItemViewHolder(ItemCartItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class CartItemViewHolder(
        private val binding: ItemCartItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            when (listener) {
                is CartDetailsFragment -> {
                    binding.buttonRemove.setOnClickListener {
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onRemoveItemClick(items[position])
                        }
                    }
                }
                is OrderResultFragment -> {
                    binding.buttonRemove.visibility = View.GONE
                }
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }

        fun bind(item: CartItem) {
            val context = itemView.context
            binding.apply {
                textViewName.text = item.name
                textViewQuantity.text = context.getString(
                    R.string.item_cart_item_text_view_quantity_format, item.quantity)
                textViewTotalPrice.text = context.getString(
                    R.string.item_cart_item_text_view_total_price_format,
                    Formatter.formatCurrency((item.price * item.quantity).toString()))
            }
        }
    }

    interface OnItemClickListener {
        fun onRemoveItemClick(item: CartItem)
    }
}
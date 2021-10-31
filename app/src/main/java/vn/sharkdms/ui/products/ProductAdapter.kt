package vn.sharkdms.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.sharkdms.R
import vn.sharkdms.databinding.ItemProductBinding
import vn.sharkdms.util.Formatter

class ProductAdapter(
    private val listener: OnItemClickListener) : PagingDataAdapter<Product, ProductAdapter
.ProductViewHolder>(
    DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonSelect.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView.context).load(product.imageUrl).error(R.drawable.ic_product)
                    .into(imageViewProduct)
                val quantity = product.quantity
                val context = itemView.context
                if (quantity <= 0L) {
                    textViewQuantity.text = context.getString(
                        R.string.item_product_text_view_quantity_out_of_stock_text)
                    buttonSelect.isEnabled = false
                    buttonSelect.setBackgroundResource(R.drawable.button_disable)
                } else {
                    textViewQuantity.text = quantity.toString()
                    buttonSelect.isEnabled = true
                    buttonSelect.setBackgroundResource(R.drawable.button_primary)
                }
                textViewName.text = product.name
                textViewPrice.text = context.getString(R.string.item_product_text_view_price_format,
                    Formatter.formatCurrency(product.price.toString()))
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(product: Product)
    }
}
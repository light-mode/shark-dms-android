package vn.sharkdms.ui.history.list

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.sharkdms.R
import vn.sharkdms.databinding.ItemOrderBinding
import vn.sharkdms.databinding.ItemOrderHeaderBinding
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Formatter

class HistoryOrderAdapter(
    private val listener: OnItemClickListener
) : PagingDataAdapter<HistoryOrderUiModel, RecyclerView.ViewHolder>(
    DiffCallBack()
) {

    inner class HistoryOrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position).let { item ->
                        val order = (item as HistoryOrderUiModel.HistoryOrderItem).historyOrder
                        listener.onItemClick(order)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(data: HistoryOrder) {
            binding.apply {
                tvCustomerName.text = data.customerName
                tvOrderCode.text = data.orderCode
                tvOrderPrice.text = Formatter.formatCurrency(data.orderTotalAmount.toString()) + " VND"
                when(data.orderStatus) {
                    Constant.ORDER_STATUS_NEW -> {
                        tvOrderStatus.text = Constant.ORDER_STATUS_NEW
                        tvOrderStatus.setTextColor(Color.parseColor("#065EA8"))
                    }
                    Constant.ORDER_STATUS_PROCESSING_QUERY -> {
                        tvOrderStatus.text = Constant.ORDER_STATUS_PROCESSING
                        tvOrderStatus.setTextColor(Color.parseColor("#E2740F"))
                    }
                    Constant.ORDER_STATUS_DONE -> {
                        tvOrderStatus.text = Constant.ORDER_STATUS_DONE
                        tvOrderStatus.setTextColor(Color.parseColor("#069450"))
                    }
                    Constant.ORDER_STATUS_CANCEL_QUERY -> {
                        tvOrderStatus.text = Constant.ORDER_STATUS_CANCEL
                        tvOrderStatus.setTextColor(Color.RED)
                    }
                    Constant.ORDER_STATUS_STOCK_OUT -> {
                        tvOrderStatus.text = Constant.ORDER_STATUS_STOCK_OUT
                        tvOrderStatus.setTextColor(Color.parseColor("#EB8275"))
                    }
                }
            }
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemOrderHeaderBinding
    ) : RecyclerView.ViewHolder(
        binding.root) {

        fun bind(text: String) {
            binding.apply {
                textViewDate.text = text
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position).let { uiModel ->
            return when (uiModel) {
                is HistoryOrderUiModel.HistoryOrderItem -> R.layout.item_order
                is HistoryOrderUiModel.HeaderItem -> R.layout.item_order_header
                else -> 0
            }
        }
    }

    class DiffCallBack: DiffUtil.ItemCallback<HistoryOrderUiModel>() {
        override fun areItemsTheSame(oldItem: HistoryOrderUiModel, newItem: HistoryOrderUiModel) =
            (oldItem is HistoryOrderUiModel.HistoryOrderItem && newItem is HistoryOrderUiModel.HistoryOrderItem &&
                    oldItem.historyOrder.orderCustomerId == newItem.historyOrder.orderCustomerId) || (oldItem is HistoryOrderUiModel
            .HeaderItem && newItem is HistoryOrderUiModel.HeaderItem && oldItem.text == newItem.text)

        override fun areContentsTheSame(oldItem: HistoryOrderUiModel, newItem: HistoryOrderUiModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position).let { uiModel ->
            when(uiModel) {
                is HistoryOrderUiModel.HistoryOrderItem -> (holder as HistoryOrderViewHolder).bind(uiModel.historyOrder)
                is HistoryOrderUiModel.HeaderItem -> (holder as HeaderViewHolder).bind(uiModel.text)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == R.layout.item_order) {
            val binding = ItemOrderBinding.inflate(inflater, parent, false)
            HistoryOrderViewHolder(binding)
        } else {
            val binding = ItemOrderHeaderBinding.inflate(inflater, parent, false)
            HeaderViewHolder(binding)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(historyOrder: HistoryOrder)
    }
}
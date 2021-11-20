package vn.sharkdms.ui.base.history.list

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.sharkdms.R
import vn.sharkdms.databinding.ItemHistoryOrderHeaderBinding
import vn.sharkdms.databinding.ItemNotificationsHeaderBinding
import vn.sharkdms.databinding.ItemOrderBinding
import vn.sharkdms.ui.history.list.HistoryOrderListFragmentDirections
import vn.sharkdms.ui.notifications.UiModel
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Formatter

class HistoryOrderAdapter(
    private val listener: OnItemClickListener
) : PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(
    DiffCallBack()
) {

    var historyOrders = ArrayList<HistoryOrder>()

    inner class HistoryOrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position).let { item ->
                        val order = (item as UiModel.HistoryOrderItem).historyOrder
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
                    Constant.ORDER_STATUS_STOCKOUT -> {
                        tvOrderStatus.text = Constant.ORDER_STATUS_STOCKOUT
                        tvOrderStatus.setTextColor(Color.parseColor("#EB8275"))
                    }
                }
            }
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemHistoryOrderHeaderBinding
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
                is UiModel.HistoryOrderItem -> R.layout.item_order
                is UiModel.HeaderItem -> R.layout.item_history_order_header
                else -> 0
            }
        }
    }

    class DiffCallBack: DiffUtil.ItemCallback<UiModel>() {
        override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel) =
            (oldItem is UiModel.HistoryOrderItem && newItem is UiModel.HistoryOrderItem &&
                    oldItem.historyOrder.orderCustomerId == newItem.historyOrder.orderCustomerId) || (oldItem is UiModel
            .HeaderItem && newItem is UiModel.HeaderItem && oldItem.text == newItem.text)

        override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position).let { uiModel ->
            when(uiModel) {
                is UiModel.HistoryOrderItem -> (holder as HistoryOrderViewHolder).bind(uiModel.historyOrder)
                is UiModel.HeaderItem -> (holder as HeaderViewHolder).bind(uiModel.text)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == R.layout.item_order) {
            val binding = ItemOrderBinding.inflate(inflater, parent, false)
            HistoryOrderViewHolder(binding)
        } else {
            val binding = ItemHistoryOrderHeaderBinding.inflate(inflater, parent, false)
            HeaderViewHolder(binding)
        }
    }

    fun setDataList(data: ArrayList<HistoryOrder>) {
        historyOrders = data
    }

    interface OnItemClickListener {
        fun onItemClick(historyOrder: HistoryOrder)
    }
}
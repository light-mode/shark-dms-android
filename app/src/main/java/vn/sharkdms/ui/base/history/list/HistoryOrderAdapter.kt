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
import vn.sharkdms.databinding.ItemOrderBinding
import vn.sharkdms.ui.history.list.HistoryOrderListFragmentDirections
import vn.sharkdms.util.Formatter

class HistoryOrderAdapter(
    private val listener: OnItemClickListener
) : PagingDataAdapter<HistoryOrder, HistoryOrderAdapter.HistoryOrderViewHolder>(
    DiffUtilCallBack()
) {

    var historyOrders = ArrayList<HistoryOrder>()

    inner class HistoryOrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
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
                    "Mới" -> {
                        tvOrderStatus.text = "Mới"
                        tvOrderStatus.setTextColor(Color.parseColor("#065EA8"))
                    }
                    "Đang xử lí" -> {
                        tvOrderStatus.text = "Đang xử lý"
                        tvOrderStatus.setTextColor(Color.parseColor("#E2740F"))
                    }
                    "Hoàn thành" -> {
                        tvOrderStatus.text = "Hoàn thành"
                        tvOrderStatus.setTextColor(Color.parseColor("#069450"))
                    }
                    "Hủy" -> {
                        tvOrderStatus.text = "Hủy"
                        tvOrderStatus.setTextColor(Color.RED)
                    }
                }
            }
        }
    }

    class DiffUtilCallBack: DiffUtil.ItemCallback<HistoryOrder>() {
        override fun areItemsTheSame(oldItem: HistoryOrder, newItem: HistoryOrder): Boolean {
            return oldItem.orderCustomerId == newItem.orderCustomerId
        }

        override fun areContentsTheSame(oldItem: HistoryOrder, newItem: HistoryOrder): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: HistoryOrderViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryOrderViewHolder {
        return HistoryOrderViewHolder(
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun setDataList(data: ArrayList<HistoryOrder>) {
        historyOrders = data
    }

    interface OnItemClickListener {
        fun onItemClick(historyOrder: HistoryOrder)
    }
}
package vn.sharkdms.ui.history

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.sharkdms.R

class HistoryOrderAdapter() : PagingDataAdapter<HistoryOrder, HistoryOrderAdapter.HistoryOrderViewHolder>(
    DiffUtilCallBack()
) {

    var historyOrders = ArrayList<HistoryOrder>()

    class HistoryOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerName = itemView.findViewById<TextView>(R.id.tv_customer_name)
        val tvOrderCode = itemView.findViewById<TextView>(R.id.tv_order_code)
        val tvOrderPrice = itemView.findViewById<TextView>(R.id.tv_order_price)
        val tvOrderStatus = itemView.findViewById<TextView>(R.id.tv_order_status)

        fun bind(data: HistoryOrder) {
            tvCustomerName.text = data.customerName
            tvOrderCode.text = data.orderCode
            tvOrderPrice.text = data.orderTotalAmount.toString()
            when(data.orderStatus) {
                "Mới" -> tvOrderStatus.text = Resources.getSystem().getString(R.string.fragment_history_order_status_new)
                "Đang xử lý" -> tvOrderStatus.text = Resources.getSystem().getString(R.string.fragment_history_order_status_processing)
                "Hoàn thành" -> tvOrderStatus.text = Resources.getSystem().getString(R.string.fragment_history_order_status_done)
                "Hủy" -> tvOrderStatus.text = Resources.getSystem().getString(R.string.fragment_history_order_status_cancel)
            }
        }
    }

    class DiffUtilCallBack: DiffUtil.ItemCallback<HistoryOrder>() {
        override fun areItemsTheSame(oldItem: HistoryOrder, newItem: HistoryOrder): Boolean {
            return oldItem.customerName == newItem.customerName
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
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_order,
                parent,
                false
            )
        )
    }

    fun setDataList(data: ArrayList<HistoryOrder>) {
        historyOrders = data
    }
}
package vn.sharkdms.ui.history.list

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
import java.util.logging.XMLFormatter

class HistoryOrderAdapter() : PagingDataAdapter<HistoryOrder, HistoryOrderAdapter.HistoryOrderViewHolder>(
    DiffUtilCallBack()
) {

    var historyOrders = ArrayList<HistoryOrder>()

    class HistoryOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerName = itemView.findViewById<TextView>(R.id.tv_customer_name)
        val tvOrderCode = itemView.findViewById<TextView>(R.id.tv_order_code)
        val tvOrderPrice = itemView.findViewById<TextView>(R.id.tv_order_price)
        val tvOrderStatus = itemView.findViewById<TextView>(R.id.tv_order_status)

        @SuppressLint("SetTextI18n")
        fun bind(data: HistoryOrder) {
            tvCustomerName.text = data.customerName
            tvOrderCode.text = data.orderCode
            tvOrderPrice.text = data.orderTotalAmount.toString()
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
        holder.itemView.setOnClickListener {
            val action = HistoryOrderListFragmentDirections.actionHistoryOrderListFragmentToOrderInfoFragment(getItem(position)!!)
            it.findNavController().navigate(action)
        }
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
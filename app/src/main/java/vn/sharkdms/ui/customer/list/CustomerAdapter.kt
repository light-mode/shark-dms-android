package vn.sharkdms.ui.customer.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.sharkdms.R
import vn.sharkdms.api.Customer
import vn.sharkdms.databinding.ItemCustomerBinding
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Formatter

class CustomerAdapter(
    private val listener: OnItemClickListener
) : PagingDataAdapter<Customer, CustomerAdapter.CustomerViewHolder>(
    DiffUtilCallBack()
) {

    private var customers = ArrayList<Customer>()

    inner class CustomerViewHolder(private val binding: ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root) {
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

        fun bind(data: Customer) {
            val context = itemView.context
            binding.apply {
                tvCustomerName.text = data.customerName
                tvCustomerPhoneNumber.text = data.customerPhone
                tvCustomerRank.text =
                    context.getString(R.string.fragment_customer_rank_format, data.rankName)
                tvCustomerAddress.text =
                    context.getString(R.string.fragment_customer_address_format,
                        Formatter.collapseDisplay(data.customerAddress, Constant.ADDRESS_LIMIT))
                tvCustomerCheckIn.text =
                    context.getString(R.string.fragment_customer_check_in_format, data.checkInDate)
                Glide.with(ivAvatarListCustomer).load(data.customerAvatar).circleCrop()
                    .into(ivAvatarListCustomer)
            }
        }
    }

    class DiffUtilCallBack: DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.customerName == newItem.customerName
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        return CustomerViewHolder(
            ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        if (!customers.contains(getItem(position))) customers.add(getItem(position)!!)
        holder.bind(getItem(position)!!)
    }

    fun getDataList(): ArrayList<Customer> {
        return customers
    }

    interface OnItemClickListener {
        fun onItemClick(customer: Customer)
    }
}
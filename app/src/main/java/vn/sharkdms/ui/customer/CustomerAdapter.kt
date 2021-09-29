package vn.sharkdms.ui.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.sharkdms.R
import vn.sharkdms.databinding.ItemCustomerBinding

class CustomerAdapter : PagingDataAdapter<Customer, CustomerAdapter.CustomerViewHolder>(DiffUtilCallBack()) {
//    private var customers = List<Customer>()

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatarListCustomer = itemView.findViewById<ImageView>(R.id.iv_avatar_list_customer)
        val tvCustomerName = itemView.findViewById<TextView>(R.id.tv_customer_name)
        val tvCustomerPhoneNumber = itemView.findViewById<TextView>(R.id.tv_customer_phone_number)
        val tvCustomerRank = itemView.findViewById<TextView>(R.id.tv_customer_rank)
        val tvCustomerAddress = itemView.findViewById<TextView>(R.id.tv_customer_address)
        val tvCustomerCheckIn = itemView.findViewById<TextView>(R.id.tv_customer_check_in)

        fun bind(data: Customer) {
            tvCustomerName.text = data.name
            tvCustomerPhoneNumber.text = data.phoneNumber
            tvCustomerRank.text = R.string.fragment_customer_rank.toString().plus(" ").plus(data.rank)
            tvCustomerAddress.text = R.string.fragment_customer_address.toString().plus(" ").plus(data.address)
            tvCustomerCheckIn.text = R.string.fragment_customer_check_in.toString().plus(" ").plus(data.checkIn.toString())
            Glide.with(ivAvatarListCustomer).load(data.avatar).circleCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(ivAvatarListCustomer)
        }
    }

    class DiffUtilCallBack: DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }

    }

//    fun setDataList(data: List<Customer>) {
//        this.customers = data
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        return CustomerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_customer,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}
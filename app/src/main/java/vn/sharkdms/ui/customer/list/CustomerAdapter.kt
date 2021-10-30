package vn.sharkdms.ui.customer.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.sharkdms.R
import vn.sharkdms.util.Constant

class CustomerAdapter() : PagingDataAdapter<Customer, CustomerAdapter.CustomerViewHolder>(
    DiffUtilCallBack()
) {

    var customers = ArrayList<Customer>()

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatarListCustomer = itemView.findViewById<ImageView>(R.id.iv_avatar_list_customer)
        val tvCustomerName = itemView.findViewById<TextView>(R.id.tv_customer_name)
        val tvCustomerPhoneNumber = itemView.findViewById<TextView>(R.id.tv_customer_phone_number)
        val tvCustomerRank = itemView.findViewById<TextView>(R.id.tv_customer_rank)
        val tvCustomerAddress = itemView.findViewById<TextView>(R.id.tv_customer_address)
        val tvCustomerCheckIn = itemView.findViewById<TextView>(R.id.tv_customer_check_in)



        fun bind(data: Customer) {
            tvCustomerName.text = data.customerName
            tvCustomerPhoneNumber.text = data.customerPhone
            tvCustomerRank.text = Constant.CUSTOMER_DETAIL_RANK.plus(data.rankName)
            tvCustomerAddress.text = Constant.CUSTOMER_DETAIL_ADDRESS
                .plus(Constant.collapseDisplay(data.customerAddress, Constant.ADDRESS_LIMIT))
            tvCustomerCheckIn.text = Constant.CUSTOMER_DETAIL_CHECK_IN.plus(data.checkInDate)
            Glide.with(ivAvatarListCustomer).load(data.customerAvatar).circleCrop()
                .into(ivAvatarListCustomer)
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
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_customer,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        if (!customers.contains(getItem(position))) customers.add(getItem(position)!!)
        holder.bind(getItem(position)!!)
        holder.itemView.setOnClickListener {
            val action = CustomerListFragmentDirections.actionCustomerListFragmentToCustomerInfoFragment(getItem(position)!!)
            it.findNavController().navigate(action)
        }
    }

    fun getDataList(): ArrayList<Customer> {
        return customers
    }
}
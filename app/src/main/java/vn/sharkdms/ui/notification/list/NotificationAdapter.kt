package vn.sharkdms.ui.notification.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.sharkdms.R
import vn.sharkdms.databinding.ItemNotificationBinding
import vn.sharkdms.databinding.ItemNotificationsHeaderBinding

class NotificationAdapter(
    private val listener: OnItemClickListener
) : PagingDataAdapter<UiModel, RecyclerView
.ViewHolder>(
    DiffCallback()
) {

    override fun getItemViewType(position: Int): Int {
        getItem(position).let { uiModel ->
            return when (uiModel) {
                is UiModel.NotificationItem -> R.layout.item_notification
                is UiModel.HeaderItem -> R.layout.item_notifications_header
                else -> 0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == R.layout.item_notification) {
            val binding = ItemNotificationBinding.inflate(inflater, parent, false)
            NotificationViewHolder(binding)
        } else {
            val binding = ItemNotificationsHeaderBinding.inflate(inflater, parent, false)
            HeaderViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position).let { uiModel ->
            when (uiModel) {
                is UiModel.NotificationItem -> (holder as NotificationViewHolder).bind(
                    uiModel.notification)
                is UiModel.HeaderItem -> (holder as HeaderViewHolder).bind(uiModel.text)
            }
        }
    }

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position).let { item ->
                        val notification = (item as UiModel.NotificationItem).notification
                        listener.onItemClick(notification)
                    }
                }
            }
        }

        fun bind(notification: Notification) {
            binding.apply {
                textViewTitle.text = notification.title
                textViewContent.text = notification.content
            }
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemNotificationsHeaderBinding) : RecyclerView.ViewHolder(
        binding.root) {

        fun bind(text: String) {
            binding.apply {
                textViewDate.text = text
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UiModel>() {
        override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel) =
            (oldItem is UiModel.NotificationItem && newItem is UiModel.NotificationItem &&
                    oldItem.notification.id == newItem.notification.id) || (oldItem is UiModel.HeaderItem && newItem is UiModel.HeaderItem && oldItem.text == newItem.text)

        override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel) = oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(notification: Notification)
    }
}
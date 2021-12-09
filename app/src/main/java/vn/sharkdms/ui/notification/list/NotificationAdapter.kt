package vn.sharkdms.ui.notification.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.sharkdms.R
import vn.sharkdms.databinding.ItemNotificationBinding
import vn.sharkdms.databinding.ItemNotificationHeaderBinding

class NotificationAdapter(
    private val listener: OnItemClickListener
) : PagingDataAdapter<NotificationUiModel, RecyclerView
.ViewHolder>(
    DiffCallback()
) {

    override fun getItemViewType(position: Int): Int {
        getItem(position).let { uiModel ->
            return when (uiModel) {
                is NotificationUiModel.NotificationItem -> R.layout.item_notification
                is NotificationUiModel.HeaderItem -> R.layout.item_notification_header
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
            val binding = ItemNotificationHeaderBinding.inflate(inflater, parent, false)
            HeaderViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position).let { uiModel ->
            when (uiModel) {
                is NotificationUiModel.NotificationItem -> (holder as NotificationViewHolder).bind(
                    uiModel.notification)
                is NotificationUiModel.HeaderItem -> (holder as HeaderViewHolder).bind(uiModel.text)
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
                        val notification = (item as NotificationUiModel.NotificationItem).notification
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
        private val binding: ItemNotificationHeaderBinding) : RecyclerView.ViewHolder(
        binding.root) {

        fun bind(text: String) {
            binding.apply {
                textViewDate.text = text
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationUiModel>() {
        override fun areItemsTheSame(oldItem: NotificationUiModel, newItem: NotificationUiModel) =
            (oldItem is NotificationUiModel.NotificationItem && newItem is NotificationUiModel.NotificationItem &&
                    oldItem.notification.id == newItem.notification.id) || (oldItem is NotificationUiModel.HeaderItem && newItem is NotificationUiModel.HeaderItem && oldItem.text == newItem.text)

        override fun areContentsTheSame(oldItem: NotificationUiModel, newItem: NotificationUiModel) = oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(notification: Notification)
    }
}
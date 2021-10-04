package vn.sharkdms.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.sharkdms.R
import vn.sharkdms.databinding.ItemTaskBinding
import vn.sharkdms.util.Constant

class TaskAdapter(
    private val listener: OnItemClickListener) : PagingDataAdapter<Task, TaskAdapter
.TaskViewHolder>(
    DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(
        binding.root) {

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

        fun bind(task: Task) {
            binding.apply {
                textViewStatus.text = task.status.toString()
                val context = itemView.context
                var status = ""
                var backgroundStatusResId = 0
                var backgroundTaskResId = 0
                when (task.status) {
                    Constant.TASK_STATUS_NEW -> {
                        status = context.getString(R.string.task_status_new)
                        backgroundStatusResId = R.drawable.bg_item_status_new
                        backgroundTaskResId = R.drawable.bg_item_task_new
                    }
                    Constant.TASK_STATUS_COMPLETED -> {
                        status = context.getString(R.string.task_status_completed)
                        backgroundStatusResId = R.drawable.bg_item_status_completed
                        backgroundTaskResId = R.drawable.bg_item_task_completed
                    }
                    Constant.TASK_STATUS_PROCESSING -> {
                        status = context.getString(R.string.task_status_processing)
                        backgroundStatusResId = R.drawable.bg_item_status_processing
                        backgroundTaskResId = R.drawable.bg_item_task_processing
                    }
                    Constant.TASK_STATUS_CHECKING -> {
                        status = context.getString(R.string.task_status_checking)
                        backgroundStatusResId = R.drawable.bg_item_status_checking
                        backgroundTaskResId = R.drawable.bg_item_task_checking
                    }
                    Constant.TASK_STATUS_NOT_COMPLETED -> {
                        status = context.getString(R.string.task_status_not_completed)
                        backgroundStatusResId = R.drawable.bg_item_status_not_completed
                        backgroundTaskResId = R.drawable.bg_item_task_not_completed
                    }
                }
                textViewStatus.text = status
                textViewStatus.setBackgroundResource(backgroundStatusResId)
                textViewName.text = task.taskName
                textViewDescription.text = task.taskDescription
                taskLayout.setBackgroundResource(backgroundTaskResId)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
    }
}
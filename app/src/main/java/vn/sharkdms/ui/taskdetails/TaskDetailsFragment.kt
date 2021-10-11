package vn.sharkdms.ui.taskdetails

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentTaskDetailsBinding
import vn.sharkdms.util.Constant

@AndroidEntryPoint
class TaskDetailsFragment : Fragment(R.layout.fragment_task_details) {

    private val args by navArgs<TaskDetailsFragmentArgs>()
    private val viewModel by viewModels<TaskDetailsViewModel>()
    private lateinit var sharedViewModel: SharedViewModel
    private var connectivity: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTaskDetailsBinding.bind(view)
        bind(binding)
        setBackIconListener(binding)
        setStatusTextViewListener(binding)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskDetailsEvent.collect { event ->
                when (event) {
                    is TaskDetailsViewModel.TaskDetailsEvent.OnSuccess -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    is TaskDetailsViewModel.TaskDetailsEvent.OnError -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is TaskDetailsViewModel.TaskDetailsEvent.OnFailure -> {
                        Toast.makeText(requireContext(),
                            getString(R.string.message_connectivity_off), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity = it ?: false }
        if (viewModel.statusSelectorShowing.value!!) {
            showStatusSelector(binding)
        }
    }

    private fun bind(binding: FragmentTaskDetailsBinding) {
        binding.apply {
            var status = ""
            var backgroundStatusResId = 0
            when (args.task.status) {
                Constant.TASK_STATUS_NEW -> {
                    status = getString(R.string.task_status_new)
                    backgroundStatusResId = R.drawable.bg_item_status_new
                    textViewStatusAnother.text = getString(R.string.task_status_processing)
                    textViewStatusAnother.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.task_processing))
                }
                Constant.TASK_STATUS_COMPLETED -> {
                    status = getString(R.string.task_status_completed)
                    backgroundStatusResId = R.drawable.bg_item_status_completed
                }
                Constant.TASK_STATUS_PROCESSING -> {
                    status = getString(R.string.task_status_processing)
                    backgroundStatusResId = R.drawable.bg_item_status_processing
                    textViewStatusAnother.text = getString(R.string.task_status_new)
                    textViewStatusAnother.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.task_new))
                }
                Constant.TASK_STATUS_CHECKING -> {
                    status = getString(R.string.task_status_checking)
                    backgroundStatusResId = R.drawable.bg_item_status_checking
                }
                Constant.TASK_STATUS_NOT_COMPLETED -> {
                    status = getString(R.string.task_status_not_completed)
                    backgroundStatusResId = R.drawable.bg_item_status_not_completed
                }
            }
            textViewStatus.text = status
            textViewStatus.setBackgroundResource(backgroundStatusResId)
            textViewName.text = args.task.taskName
            textViewDescription.text = args.task.taskDescription
        }
    }

    private fun setBackIconListener(binding: FragmentTaskDetailsBinding) {
        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setStatusTextViewListener(binding: FragmentTaskDetailsBinding) {
        if (args.task.status != Constant.TASK_STATUS_NEW && args.task.status != Constant
                .TASK_STATUS_PROCESSING) return
        binding.apply {
            iconArrowDrop.visibility = View.VISIBLE
            textViewStatus.setOnClickListener {
                toggleStatusSelector(binding)
            }
            setStatusSelectorListener(binding)
        }
    }

    private fun setStatusSelectorListener(binding: FragmentTaskDetailsBinding) {
        setAnotherStatusTextViewListener(binding)
        setCompletedStatusTextViewListener(binding)
        setCheckingStatusTextViewListener(binding)
        setNotCompletedStatusTextViewListener(binding)
    }

    private fun setAnotherStatusTextViewListener(binding: FragmentTaskDetailsBinding) {
        binding.textViewStatusAnother.setOnClickListener {
            if (connectivity) {
                if (args.task.status == Constant.TASK_STATUS_NEW) updateTaskStatus(
                    Constant.TASK_STATUS_PROCESSING)
                else updateTaskStatus(Constant.TASK_STATUS_NEW)
            } else showConnectivityOffMessage()
        }
    }

    private fun setCompletedStatusTextViewListener(binding: FragmentTaskDetailsBinding) {
        binding.textViewStatusCompleted.setOnClickListener {
            if (connectivity) updateTaskStatus(
                Constant.TASK_STATUS_COMPLETED) else showConnectivityOffMessage()
        }
    }

    private fun setCheckingStatusTextViewListener(binding: FragmentTaskDetailsBinding) {
        binding.textViewStatusChecking.setOnClickListener {
            if (connectivity) updateTaskStatus(
                Constant.TASK_STATUS_CHECKING) else showConnectivityOffMessage()
        }
    }

    private fun setNotCompletedStatusTextViewListener(binding: FragmentTaskDetailsBinding) {
        binding.textViewStatusNotCompleted.setOnClickListener {
            if (connectivity) updateTaskStatus(
                Constant.TASK_STATUS_NOT_COMPLETED) else showConnectivityOffMessage()
        }
    }

    private fun showConnectivityOffMessage() {
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }

    private fun toggleStatusSelector(binding: FragmentTaskDetailsBinding) {
        if (viewModel.statusSelectorShowing.value!!) {
            hideStatusSelector(binding)
            viewModel.statusSelectorShowing.postValue(false)
        } else {
            showStatusSelector(binding)
            viewModel.statusSelectorShowing.postValue(true)
        }
    }

    private fun hideStatusSelector(binding: FragmentTaskDetailsBinding) {
        binding.apply {
            iconArrowDrop.setImageResource(R.drawable.ic_arrow_drop_down)
            if (args.task.status == Constant.TASK_STATUS_NEW) {
                textViewStatus.setBackgroundResource(R.drawable.bg_item_status_new)
            } else {
                textViewStatus.setBackgroundResource(R.drawable.bg_item_status_processing)
            }
            statusSelector.visibility = View.GONE
        }
    }

    private fun showStatusSelector(binding: FragmentTaskDetailsBinding) {
        binding.apply {
            iconArrowDrop.setImageResource(R.drawable.ic_arrow_drop_up)
            if (args.task.status == Constant.TASK_STATUS_NEW) {
                textViewStatus.setBackgroundResource(R.drawable.bg_item_status_new_drop)
            } else {
                textViewStatus.setBackgroundResource(R.drawable.bg_item_status_processing_drop)
            }
            statusSelector.visibility = View.VISIBLE
        }
    }

    private fun updateTaskStatus(status: Int) {
        viewModel.updateTaskStatus(sharedViewModel.token, args.task.id, status)
    }
}
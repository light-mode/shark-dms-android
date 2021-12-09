package vn.sharkdms.ui.task.details

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
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
import vn.sharkdms.util.ConfirmDialogFragment
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Utils
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class TaskDetailsFragment : Fragment(R.layout.fragment_task_details) {

    private val args by navArgs<TaskDetailsFragmentArgs>()
    private val viewModel by viewModels<TaskDetailsViewModel>()
    private lateinit var sharedViewModel: SharedViewModel

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
                        Utils.showConnectivityOffMessage(requireContext())
                    }
                    is TaskDetailsViewModel.TaskDetailsEvent.ShowUnauthorizedDialog -> {
                        Utils.showUnauthorizedDialog(requireActivity())
                    }
                }
            }
        }
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        if (viewModel.statusSelectorShowing.value!!) {
            showStatusSelector(binding)
        }
        setFragmentResultListener(ConfirmDialogFragment.TAG) { _, bundle ->
            if (Dialog.BUTTON_POSITIVE == bundle.getInt(ConfirmDialogFragment.CHANGE_TASK_STATUS)) {
                if (!sharedViewModel.connectivity.value!!) {
                    Utils.showConnectivityOffMessage(requireContext())
                    return@setFragmentResultListener
                }
                viewModel.updateTaskStatus(sharedViewModel.token, args.task.id, viewModel.selectedStatus)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
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
            textViewName.setText(args.task.taskName)
            textViewDescription.setText(args.task.taskDescription)
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
        setCheckingStatusTextViewListener(binding)
    }

    private fun setAnotherStatusTextViewListener(binding: FragmentTaskDetailsBinding) {
        binding.textViewStatusAnother.setOnClickListener {
            if (args.task.status == Constant.TASK_STATUS_NEW) viewModel.selectedStatus = Constant.TASK_STATUS_PROCESSING
            else viewModel.selectedStatus = Constant.TASK_STATUS_NEW
            updateTaskStatus()
        }
    }

    private fun setCheckingStatusTextViewListener(binding: FragmentTaskDetailsBinding) {
        binding.textViewStatusChecking.setOnClickListener {
            viewModel.selectedStatus = Constant.TASK_STATUS_CHECKING
            updateTaskStatus()
        }
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

    private fun updateTaskStatus() {
        val currentStatusString = getString(when (args.task.status) {
            Constant.TASK_STATUS_NEW -> R.string.task_status_new
            Constant.TASK_STATUS_PROCESSING -> R.string.task_status_processing
            else -> throw IllegalArgumentException()
        })
        val selectedStatusString = getString(when (viewModel.selectedStatus) {
            Constant.TASK_STATUS_NEW -> R.string.task_status_new
            Constant.TASK_STATUS_PROCESSING -> R.string.task_status_processing
            Constant.TASK_STATUS_CHECKING -> R.string.task_status_checking
            else -> throw IllegalArgumentException()
        })
        val title = getString(R.string.fragment_task_details_dialog_change_task_status_title)
        val message = getString(R.string.fragment_task_details_dialog_change_task_status_message_format, currentStatusString, selectedStatusString)
        val action = TaskDetailsFragmentDirections.actionGlobalConfirmDialog2(title, message, ConfirmDialogFragment.CHANGE_TASK_STATUS)
        findNavController().navigate(action)
    }
}
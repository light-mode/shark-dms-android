package vn.sharkdms.ui.tasks

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentTasksBinding
import vn.sharkdms.util.Constant
import java.util.*

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.OnItemClickListener {

    private val viewModel by viewModels<TasksViewModel>()
    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTasksBinding.bind(view)
        val adapter = TaskAdapter(this)
        setMenuIconListener(binding)
        setDateTextViewListener(binding)
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        adapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                if (combinedLoadStates.source.refresh is LoadState.NotLoading &&
                    combinedLoadStates.append.endOfPaginationReached && adapter.itemCount == 0) {
                    iconNoTask.visibility = View.VISIBLE
                    textViewNoTask.visibility = View.VISIBLE
                } else {
                    iconNoTask.visibility = View.GONE
                    textViewNoTask.visibility = View.GONE
                }
            }
        }
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.searchTasks(sharedViewModel.token, "")
    }

    private fun setMenuIconListener(binding: FragmentTasksBinding) {
        binding.iconMenu.setOnClickListener {
            (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
        }
    }

    private fun setDateTextViewListener(binding: FragmentTasksBinding) {
        setDateTextViewOnClickListener(binding)
        setDateTextViewOnTouchListener(binding)
    }

    private fun setDateTextViewOnClickListener(binding: FragmentTasksBinding) {
        binding.textViewDate.setOnClickListener {
            val calendar = getDisplayCalendar()
            val dialog = DatePickerDialog(requireContext(), R.style.date_picker_dialog,
                { _, year, month, dayOfMonth ->
                    val searchDate = getString(R.string.fragment_tasks_date_format_search,
                        dayOfMonth, month + 1, year)
                    val displayDate = getString(R.string.fragment_tasks_date_format_display,
                        dayOfMonth, month + 1, year)
                    viewModel.searchTasks(sharedViewModel.token, searchDate)
                    binding.textViewDate.apply {
                        text = displayDate
                        setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_date_picker,
                            0, R.drawable.ic_clear, 0)
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE))
            dialog.show()
            val primaryColor = Color.parseColor(getString(R.string.color_primary))
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(primaryColor)
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(primaryColor)
        }
    }

    private fun getDisplayCalendar(): Calendar {
        val searchDate = viewModel.currentSearchDate.value!!
        val calendar = Calendar.getInstance()
        if (searchDate.isEmpty()) return calendar
        val date = searchDate.split('-')
        val dayOfMonth = date[0].toInt()
        val month = date[1].toInt() - 1
        val year = date[2].toInt()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        return calendar
    }

    private fun setDateTextViewOnTouchListener(binding: FragmentTasksBinding) {
        binding.textViewDate.apply {
            setOnTouchListener(object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                    val clearIcon = AppCompatResources.getDrawable(requireContext(),
                        R.drawable.ic_clear)
                    if (view == null || event == null || clearIcon == null || event.action !=
                        MotionEvent.ACTION_UP) return false
                    if (event.rawX < this@apply.right - clearIcon.bounds.width() - this@apply
                            .paddingEnd * 2) return false
                    viewModel.searchTasks(sharedViewModel.token, "")
                    this@apply.apply {
                        text = getString(R.string.fragment_tasks_text_view_date_default_text)
                        setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_date_picker,
                            0, 0, 0)
                    }
                    return true
                }
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onItemClick(task: Task) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskDetailsFragment(task)
        findNavController().navigate(action)
    }
}
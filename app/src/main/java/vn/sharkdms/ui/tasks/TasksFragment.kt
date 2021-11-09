package vn.sharkdms.ui.tasks

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.OnItemClickListener {

    private val viewModel by viewModels<TasksViewModel>()
    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTasksBinding.bind(view)
        val adapter = TaskAdapter(this)
        binding.apply {
            iconMenu.setOnClickListener {
                (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
            }
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
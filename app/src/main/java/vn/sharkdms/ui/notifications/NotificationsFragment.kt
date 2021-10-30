package vn.sharkdms.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentNotificationsBinding

@AndroidEntryPoint
class NotificationsFragment : Fragment(
    R.layout.fragment_notifications), NotificationAdapter.OnItemClickListener {

    private val viewModel by viewModels<NotificationsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentNotificationsBinding.bind(view)
        val adapter = NotificationAdapter(this)
        binding.apply {
            iconBack.setOnClickListener {
                findNavController().navigateUp()
            }
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
        adapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                if (combinedLoadStates.source.refresh is LoadState.NotLoading &&
                    combinedLoadStates.append.endOfPaginationReached && adapter.itemCount == 0) {
                    iconNoNotification.visibility = View.VISIBLE
                    textViewNoNotification.visibility = View.VISIBLE
                } else {
                    iconNoNotification.visibility = View.GONE
                    textViewNoNotification.visibility = View.GONE
                }
            }
        }
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.getNotifications(sharedViewModel.token).observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onItemClick(notification: Notification) {
        val action = NotificationsFragmentDirections
            .actionNotificationsFragmentToNotificationDetailsFragment(
            notification)
        findNavController().navigate(action)
    }
}
package vn.sharkdms.ui.notificationdetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentNotificationDetailsBinding

@AndroidEntryPoint
class NotificationDetailsFragment : Fragment(R.layout.fragment_notification_details) {

    private val args by navArgs<NotificationDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentNotificationDetailsBinding.bind(view)
        bind(binding)
        setBackIconListener(binding)
    }

    private fun setBackIconListener(binding: FragmentNotificationDetailsBinding) {
        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bind(binding: FragmentNotificationDetailsBinding) {
        val notification = args.notification
        binding.apply {
            textViewTitle.text = notification.title
            textViewContent.text = notification.content
        }
    }
}
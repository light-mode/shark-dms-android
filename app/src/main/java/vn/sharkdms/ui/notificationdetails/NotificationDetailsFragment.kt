package vn.sharkdms.ui.notificationdetails

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentNotificationDetailsBinding
import vn.sharkdms.util.Constant

@AndroidEntryPoint
class NotificationDetailsFragment : Fragment(R.layout.fragment_notification_details) {

    private val args by navArgs<NotificationDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentNotificationDetailsBinding.bind(view)
        bind(binding)
        setBackIconListener(binding)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
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
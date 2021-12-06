package vn.sharkdms.ui.report

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.SharedViewModel
import vn.sharkdms.api.GetReportResponse
import vn.sharkdms.databinding.FragmentReportBinding
import vn.sharkdms.util.ConfirmDialogFragment
import vn.sharkdms.util.MessageDialogFragment
import vn.sharkdms.util.Utils

@AndroidEntryPoint
class ReportFragment : Fragment(R.layout.fragment_report) {

    private val viewModel by viewModels<ReportViewModel>()
    private lateinit var sharedViewModel: SharedViewModel
    private var connectivity = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentReportBinding.bind(view)
        setMenuIconListener(binding)
        setTitleEditTextListener(binding)
        setDescriptionEditTextListener(binding)
        setSendButtonListener(binding)
        setEventListener(binding)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.connectivity.observe(viewLifecycleOwner) {
            connectivity = it
            val mustLoad = viewModel.reportId == 0
            if (connectivity && mustLoad) {
                viewModel.getReport(sharedViewModel.token)
            } else if (mustLoad) {
                binding.progressBar1.visibility = View.GONE
                Utils.showConnectivityOffMessage(requireContext())
            } else {
                binding.apply {
                    progressBar1.visibility = View.GONE
                    cardViewTitle.visibility = View.VISIBLE
                    cardViewDescription.visibility = View.VISIBLE
                    buttonSend.visibility = View.VISIBLE
                    editTextTitle.setText(viewModel.reportTitle)
                    editTextDescription.setText(viewModel.reportDescription)
                }
            }
        }

        Utils.setupUI(binding.reportFragment, requireActivity() as AppCompatActivity)
        setFragmentResultListener(ConfirmDialogFragment.TAG) { _, bundle ->
            if (!connectivity) {
                Utils.showConnectivityOffMessage(requireContext())
                return@setFragmentResultListener
            }
            if (Dialog.BUTTON_POSITIVE == bundle.getInt(ConfirmDialogFragment.SEND_REPORT)) {
                doBeforeRequest(binding)
                viewModel.createOrEditReport(sharedViewModel.token)
            }
        }
        setFragmentResultListener(MessageDialogFragment.TAG) { _, bundle ->
            when (Dialog.BUTTON_POSITIVE) {
                bundle.getInt(MessageDialogFragment.CREATE_REPORT) -> {
                    if (connectivity) viewModel.getReportAfterCreate(sharedViewModel.token)
                    else Utils.showConnectivityOffMessage(requireContext())
                }
                bundle.getInt(MessageDialogFragment.EDIT_REPORT) -> {
                    //
                }
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

    private fun setMenuIconListener(binding: FragmentReportBinding) {
        binding.iconMenu.setOnClickListener {
            (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
        }
    }

    private fun setTitleEditTextListener(binding: FragmentReportBinding) {
        binding.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.reportTitle = p0.toString()
                checkEnableSendButton(binding)
            }
        })
    }

    private fun setDescriptionEditTextListener(binding: FragmentReportBinding) {
        binding.editTextDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.reportDescription = p0.toString()
                checkEnableSendButton(binding)
            }
        })
    }

    private fun checkEnableSendButton(binding: FragmentReportBinding) {
        val title = viewModel.reportTitle
        val description = viewModel.reportDescription
        binding.apply {
            if (title.isBlank() || description.isBlank()) {
                buttonSend.isEnabled = false
                buttonSend.setBackgroundResource(R.drawable.button_disable)
            } else {
                buttonSend.isEnabled = true
                buttonSend.setBackgroundResource(R.drawable.button_primary)
            }
        }
    }

    private fun setSendButtonListener(binding: FragmentReportBinding) {
        binding.buttonSend.setOnClickListener {
            val title = getString(R.string.dialog_message_title_text)
            val message = getString(R.string.fragment_report_message_confirm)
            val action = ReportFragmentDirections.actionGlobalConfirmDialog2(title, message, ConfirmDialogFragment.SEND_REPORT)
            findNavController().navigate(action)
        }
    }

    private fun setEventListener(binding: FragmentReportBinding) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.reportEvent.collect { event ->
                when (event) {
                    is ReportViewModel.ReportEvent.OnGetReportResponse -> {
                        handleGetReportResponse(binding, event.data)
                    }
                    is ReportViewModel.ReportEvent.OnCreateReportResponse -> {
                        handleCreateReportResponse(binding, event.message)
                    }
                    is ReportViewModel.ReportEvent.OnEditReportResponse -> {
                        handleEditReportResponse(binding, event.message)
                    }
                    is ReportViewModel.ReportEvent.OnFailure -> {
                        handleRequestFailure(binding)
                    }
                    is ReportViewModel.ReportEvent.ShowUnauthorizedDialog -> {
                        Utils.showUnauthorizedDialog(requireActivity())
                    }
                }
            }
        }
    }

    private fun doBeforeRequest(binding: FragmentReportBinding) {
        binding.apply {
            buttonSend.isEnabled = false
            buttonSend.text = ""
            progressBar2.visibility = View.VISIBLE
            editTextTitle.clearFocus()
            editTextDescription.clearFocus()
        }
    }

    private fun handleGetReportResponse(binding: FragmentReportBinding,
        data: GetReportResponse?) {
        binding.apply {
            progressBar1.visibility = View.GONE
            cardViewTitle.visibility = View.VISIBLE
            cardViewDescription.visibility = View.VISIBLE
            buttonSend.visibility = View.VISIBLE
        }
        if (data != null) {
            binding.apply {
                editTextTitle.setText(data.title)
                editTextDescription.setText(data.description)
            }
        }
    }

    private fun handleCreateReportResponse(binding: FragmentReportBinding, message: String) {
        doAfterResponse(binding)
        val action = ReportFragmentDirections.actionGlobalMessageDialog3(message, MessageDialogFragment.CREATE_REPORT)
        findNavController().navigate(action)
    }

    private fun handleEditReportResponse(binding: FragmentReportBinding, message: String) {
        doAfterResponse(binding)
        val action = ReportFragmentDirections.actionGlobalMessageDialog3(message, MessageDialogFragment.EDIT_REPORT)
        findNavController().navigate(action)
    }

    private fun handleRequestFailure(binding: FragmentReportBinding) {
        doAfterResponse(binding)
        Utils.showConnectivityOffMessage(requireContext())
    }

    private fun doAfterResponse(binding: FragmentReportBinding) {
        binding.apply {
            progressBar1.visibility = View.GONE
            progressBar2.visibility = View.GONE
            buttonSend.text = getString(R.string.fragment_report_button_send_text)
            buttonSend.isEnabled = true
        }
    }
}
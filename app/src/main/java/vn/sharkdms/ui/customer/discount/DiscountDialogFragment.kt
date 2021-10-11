package vn.sharkdms.ui.customer.discount

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_discount_dialog.view.*
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.ui.forgotpassword.ForgotPasswordFragment
import vn.sharkdms.ui.overview.OverviewViewModel
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus

class DiscountDialogFragment : DialogFragment() {

    private val TAG = "DiscountDialogFragment"
    private var customerId: Int? = 0
    private lateinit var viewModel: DiscountDialogViewModel
    private lateinit var sharedViewModel : SharedViewModel
    private var connectivity: Boolean = true

    fun newInstance(id: Int): DiscountDialogFragment {
        val args = Bundle()
        args.putInt("id", id)
        val fragment = DiscountDialogFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_discount_dialog, container, false)

        customerId = arguments?.getInt("id")

        viewModel = ViewModelProvider(requireActivity())[DiscountDialogViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity = it ?: false }

        initDiscountTable(rootView)
        setBtnCloseOnClickListener(rootView)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.discountDialogEvent.collect { event ->
                when(event) {
                    is DiscountDialogViewModel.DiscountDialogEvent.OnResponse -> {
                        if (event.data?.size  != 0)
                            handleGetDiscountInfoResponse(rootView, event.code, event.message, event.data?.get(0))
                        else
                            handleGetDiscountInfoResponse(rootView, event.code, event.message, null)
                    }
                    is DiscountDialogViewModel.DiscountDialogEvent.OnFailure ->
                        handleRequestFailure()
                }
            }
        }

        return rootView
    }

    private fun initDiscountTable(rootView: View) {
        rootView.apply {
            if (!connectivity) {
                Toast.makeText(requireContext(),
                    getString(R.string.message_connectivity_off), Toast.LENGTH_LONG).show()
                return
            }
            viewModel.sendGetDiscountInfo(sharedViewModel.token, customerId)
        }
    }

    private fun setBtnCloseOnClickListener(rootView: View) {
        rootView.iv_close.setOnClickListener {
            dismiss()
        }
    }

    private fun handleGetDiscountInfoResponse(rootView: View, code: Int, message: String, data: DiscountInfo?) {
        rootView.apply {
            when (code) {
                HttpStatus.OK -> {
                    if (data != null) {
                        when (data.ruleCode) {
                            "max" -> {
                                row_min.visibility = View.VISIBLE
                                row_min_max.visibility = View.VISIBLE
                                row_max.visibility = View.VISIBLE
                            }
                            "min_max" -> {
                                row_min.visibility = View.VISIBLE
                                row_min_max.visibility = View.VISIBLE
                            }
                            "min" -> {
                                row_min.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        tv_customer_discount_none_message.visibility = View.VISIBLE
                    }
                }
                HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> Toast.makeText(requireContext(),
                    message, Toast.LENGTH_SHORT).show()
                else -> Log.e(TAG, code.toString())
            }
        }
    }

    private fun handleRequestFailure() {
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }
}
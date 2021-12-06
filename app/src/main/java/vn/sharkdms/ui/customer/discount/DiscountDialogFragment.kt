package vn.sharkdms.ui.customer.discount

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_discount_dialog.view.*
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.util.Utils

class DiscountDialogFragment : DialogFragment() {

    private var discountInfo: String? = ""
    private var check: Int? = 0
    private lateinit var sharedViewModel : SharedViewModel

    fun newInstance(discountInfo: String, check: Int): DiscountDialogFragment {
        val args = Bundle()
        args.putString("discountInfo", discountInfo)
        args.putInt("check", check)
        val fragment = DiscountDialogFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_discount_dialog, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        discountInfo = arguments?.getString("discountInfo")
        check = arguments?.getInt("check")
        if (check == 1) rootView.tv_customer_discount_title.text = getString(R.string.fragment_customer_discount_title_sale_account)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        initDiscountTable(rootView)
        setBtnCloseOnClickListener(rootView)

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun initDiscountTable(rootView: View) {
        rootView.apply {
            if (sharedViewModel.connectivity.value != true) {
                Utils.showConnectivityOffMessage(requireContext())
                return
            }
            when(discountInfo) {
                "max" -> {
                    row_min.visibility = View.VISIBLE
                    row_min_max.visibility = View.VISIBLE
                    row_max.visibility = View.VISIBLE
                }
                "min_max" -> {
                    row_min.visibility = View.VISIBLE
                    row_min_max.visibility = View.VISIBLE
                }
                "min" -> row_min.visibility = View.VISIBLE
                "" -> tv_customer_discount_none_message.visibility = View.VISIBLE
            }
        }
    }

    private fun setBtnCloseOnClickListener(rootView: View) {
        rootView.iv_close.setOnClickListener {
            dismiss()
        }
    }

}
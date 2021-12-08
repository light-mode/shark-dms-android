package vn.sharkdms.ui.customer.discount

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_discount_dialog.view.*
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Formatter
import vn.sharkdms.util.Utils

class DiscountDialogFragment : DialogFragment() {

    private var infoMin: DiscountInfo? = null
    private var infoMinMax: DiscountInfo? = null
    private var infoMax: DiscountInfo? = null
    private val listInfo = ArrayList<DiscountInfo?>()
    private var check: Int? = 0
    private lateinit var sharedViewModel : SharedViewModel

    fun newInstance(min: DiscountInfo?, min_max: DiscountInfo?, max: DiscountInfo?, check: Int): DiscountDialogFragment {
        val args = Bundle()
        args.putInt("check", check)
        args.putParcelable("min", min)
        args.putParcelable("min_max", min_max)
        args.putParcelable("max", max)
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

        infoMin = arguments?.getParcelable<DiscountInfo>("min")
        infoMinMax = arguments?.getParcelable<DiscountInfo>("min_max")
        infoMax = arguments?.getParcelable<DiscountInfo>("max")
        if (infoMin != null) listInfo.add(infoMin)
        if (infoMinMax != null) listInfo.add(infoMinMax)
        if (infoMax != null) listInfo.add(infoMax)
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

    @SuppressLint("SetTextI18n")
    private fun initDiscountTable(rootView: View) {
        rootView.apply {
            if (sharedViewModel.connectivity.value != true) {
                Utils.showConnectivityOffMessage(requireContext())
                return
            }
            for (info in listInfo) {
                when(info?.ruleCode) {
                    "max" -> {
                        row_max.visibility = View.VISIBLE
                        tv_customer_discount_threshold_max.text = Constant.DISCOUNT_MAX + Formatter.formatCurrency(info.maxAmount.toString())
                        tv_customer_discount_percent_max.text = "${info.discountRate}%"
                    }
                    "min_max" -> {
                        row_min_max.visibility = View.VISIBLE
                        tv_customer_discount_threshold_min_max.text =
                            Formatter.formatCurrency(info.minAmount.toString()) + Constant.DISCOUNT_MIN_MAX + Formatter.formatCurrency(info.maxAmount.toString())
                        tv_customer_discount_percent_min_max.text = "${info.discountRate}%"
                    }
                    "min" -> {
                        row_min.visibility = View.VISIBLE
                        tv_customer_discount_threshold_min.text = Formatter.formatCurrency(info.minAmount.toString()) + Constant.DISCOUNT_MIN
                        tv_customer_discount_percent_min.text = "${info.discountRate}%"
                    }
                    "" -> tv_customer_discount_none_message.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setBtnCloseOnClickListener(rootView: View) {
        rootView.iv_close.setOnClickListener {
            dismiss()
        }
    }

}
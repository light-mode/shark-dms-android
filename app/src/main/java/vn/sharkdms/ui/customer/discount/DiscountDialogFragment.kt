package vn.sharkdms.ui.customer.discount

import android.annotation.SuppressLint
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
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Formatter
import vn.sharkdms.util.Utils

class DiscountDialogFragment : DialogFragment() {

    private var discountRule: String? = ""
    private var discountMinAmount: String? = ""
    private var discountMaxAmount: String? = ""
    private var discountRate: String? = ""
    private var check: Int? = 0
    private lateinit var sharedViewModel : SharedViewModel

    fun newInstance(rule: String, min: String?, max: String?, rate: String?, check: Int): DiscountDialogFragment {
        val args = Bundle()
        args.putString("rule", rule)
        args.putString("min", min)
        args.putString("max", max)
        args.putString("rate", rate)
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

        discountRule = arguments?.getString("rule")
        discountMinAmount = arguments?.getString("min")
        discountMaxAmount = arguments?.getString("max")
        discountRate = arguments?.getString("rate")
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
            if (check == 0) {
                when(discountRule) {
                    "max" -> {
                        row_max.visibility = View.VISIBLE
                        tv_customer_discount_threshold_max.text = Constant.DISCOUNT_MAX + Formatter.formatCurrency(discountMaxAmount!!)
                        tv_customer_discount_percent_max.text = "$discountRate%"
                    }
                    "min_max" -> {
                        row_min_max.visibility = View.VISIBLE
                        tv_customer_discount_threshold_min_max.text =
                            Formatter.formatCurrency(discountMinAmount!!) + Constant.DISCOUNT_MIN_MAX + Formatter.formatCurrency(discountMaxAmount!!)
                        tv_customer_discount_percent_min_max.text = "$discountRate%"
                    }
                    "min" -> {
                        row_min.visibility = View.VISIBLE
                        tv_customer_discount_threshold_min.text = discountMinAmount + Formatter.formatCurrency(discountMinAmount!!)
                        tv_customer_discount_percent_min.text = "$discountRate%"
                    }
                    "" -> tv_customer_discount_none_message.visibility = View.VISIBLE
                }
            } else {
                when(discountRule) {
                    "max" -> {
                        row_min.visibility = View.VISIBLE
                        tv_customer_discount_threshold_min.text = Formatter.formatCurrencyDot(discountMinAmount!!) + Constant.DISCOUNT_MIN
                        tv_customer_discount_percent_min.text = "$discountRate%"
                        row_min_max.visibility = View.VISIBLE
                        tv_customer_discount_threshold_min_max.text =
                            Formatter.formatCurrencyDot(discountMinAmount!!) + Constant.DISCOUNT_MIN_MAX + Formatter.formatCurrencyDot(discountMaxAmount!!)
                        tv_customer_discount_percent_min_max.text = "$discountRate%"
                        row_max.visibility = View.VISIBLE
                        tv_customer_discount_threshold_max.text = Constant.DISCOUNT_MAX + Formatter.formatCurrencyDot(discountMaxAmount!!)
                        tv_customer_discount_percent_max.text = "$discountRate%"
                    }
                    "min_max" -> {
                        row_min.visibility = View.VISIBLE
                        tv_customer_discount_threshold_min.text = Formatter.formatCurrencyDot(discountMinAmount!!) + Constant.DISCOUNT_MIN
                        tv_customer_discount_percent_min.text = "$discountRate%"
                        row_min_max.visibility = View.VISIBLE
                        tv_customer_discount_threshold_min_max.text =
                            Formatter.formatCurrencyDot(discountMinAmount!!) + Constant.DISCOUNT_MIN_MAX + Formatter.formatCurrencyDot(discountMaxAmount!!)
                        tv_customer_discount_percent_min_max.text = "$discountRate%"
                    }
                    "min" -> {
                        row_min.visibility = View.VISIBLE
                        tv_customer_discount_threshold_min.text = Formatter.formatCurrencyDot(discountMinAmount!!) + Constant.DISCOUNT_MIN
                        tv_customer_discount_percent_min.text = "$discountRate%"
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
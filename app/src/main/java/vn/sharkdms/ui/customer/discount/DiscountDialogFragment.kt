package vn.sharkdms.ui.customer.discount

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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

class DiscountDialogFragment : DialogFragment() {

    private var discountInfo: String? = ""
    private var check: Int? = 0
    private lateinit var sharedViewModel : SharedViewModel
    private var connectivity: Boolean = true

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
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_discount_dialog, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        discountInfo = arguments?.getString("discountInfo")
        check = arguments?.getInt("check")
        if (check == 1) rootView.tv_customer_discount_title.text = getString(R.string.fragment_customer_discount_title_sale_account)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity = it ?: false }

        initDiscountTable(rootView)
        setBtnCloseOnClickListener(rootView)

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun initDiscountTable(rootView: View) {
        rootView.apply {
            if (!connectivity) {
                Toast.makeText(requireContext(),
                    getString(R.string.message_connectivity_off), Toast.LENGTH_LONG).show()
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
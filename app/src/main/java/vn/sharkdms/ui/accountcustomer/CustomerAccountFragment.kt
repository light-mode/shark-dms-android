package vn.sharkdms.ui.accountcustomer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.ui.base.account.BaseAccountFragment
import vn.sharkdms.ui.logout.LogoutDialogFragment

class CustomerAccountFragment : BaseAccountFragment() {
    private val TAG = "CustomerAccountFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAccountBinding.bind(view)
        editUI(binding)
    }

    private fun editUI(binding: FragmentAccountBinding) {
        binding.apply {
            tvAccountTitle.text = getString(R.string.fragment_customer_info_title)
            cardViewLogOut.visibility = View.VISIBLE
            cardViewLogOut.setOnClickListener {
                val dialog = LogoutDialogFragment()
                dialog.show(requireFragmentManager(), TAG)
            }
        }
    }
}
package vn.sharkdms.ui.policy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.databinding.FragmentPolicyBinding

@AndroidEntryPoint
class PolicyFragment : Fragment(R.layout.fragment_policy) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPolicyBinding.bind(view)
        setMenuIconListener(binding)
    }

    private fun setMenuIconListener(binding: FragmentPolicyBinding) {
        binding.iconMenu.setOnClickListener {
            (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
        }
    }
}
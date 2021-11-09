package vn.sharkdms.ui.policy

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.databinding.FragmentPolicyBinding
import vn.sharkdms.util.Constant

@AndroidEntryPoint
class PolicyFragment : Fragment(R.layout.fragment_policy) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPolicyBinding.bind(view)
        setMenuIconListener(binding)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun setMenuIconListener(binding: FragmentPolicyBinding) {
        binding.iconMenu.setOnClickListener {
            (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
        }
    }
}
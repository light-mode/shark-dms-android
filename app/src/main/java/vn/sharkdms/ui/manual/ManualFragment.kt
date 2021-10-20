package vn.sharkdms.ui.manual

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.databinding.FragmentManualBinding

@AndroidEntryPoint
class ManualFragment : Fragment(R.layout.fragment_manual) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentManualBinding.bind(view)
        setMenuIconListener(binding)
    }

    private fun setMenuIconListener(binding: FragmentManualBinding) {
        binding.iconMenu.setOnClickListener {
            (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
        }
    }
}
package vn.sharkdms.ui.overview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.databinding.FragmentOverviewBinding

@AndroidEntryPoint
class OverviewFragment : Fragment(R.layout.fragment_overview) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOverviewBinding.bind(view)
        binding.iconMenu.setOnClickListener {
            (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
        }
    }
}
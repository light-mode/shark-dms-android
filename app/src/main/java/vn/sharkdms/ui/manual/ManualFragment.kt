package vn.sharkdms.ui.manual

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.databinding.FragmentManualBinding
import vn.sharkdms.util.Constant

@AndroidEntryPoint
class ManualFragment : Fragment(R.layout.fragment_manual) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentManualBinding.bind(view)
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

    private fun setMenuIconListener(binding: FragmentManualBinding) {
        binding.iconMenu.setOnClickListener {
            (requireActivity() as SaleActivity).toggleNavigationDrawer(it)
        }
    }
}
package vn.sharkdms.ui.account

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.ui.base.account.BaseAccountFragment

@AndroidEntryPoint
class AccountFragment : BaseAccountFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAccountBinding.bind(view)
        setChangePasswordCardViewListener(binding)
        setAvatarImageViewListener(binding)
    }

    private fun setChangePasswordCardViewListener(binding: FragmentAccountBinding) {
        binding.cardViewChangePassword.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToChangePasswordFragment()
            findNavController().navigate(action)
        }
    }

    private fun setAvatarImageViewListener(binding: FragmentAccountBinding) {
        binding.imageViewAvatar.setOnClickListener {
            val action = AccountFragmentDirections.actionGlobalImageChooserDialog()
            findNavController().navigate(action)
        }
    }
}
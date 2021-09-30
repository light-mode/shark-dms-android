package vn.sharkdms.ui.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.data.User
import vn.sharkdms.databinding.FragmentAccountBinding

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {
    private val viewModel by viewModels<AccountViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAccountBinding.bind(view)
        binding.apply {
            iconBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.accountEvent.collect { event ->
                when (event) {
                    is AccountViewModel.AccountEvent.BindUserInfoView -> bindUserInfoView(binding,
                        event.user)
                }
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.getUserInfo(it)
        }
    }

    private fun bindUserInfoView(binding: FragmentAccountBinding, user: User) {
        binding.apply {
            Glide.with(this@AccountFragment).load(user.avatar).error(R.drawable.ic_avatar)
                .into(imageViewAvatar)
            textViewName.text = user.name
            textViewPhone.text = user.phone
            textViewUsername.text = user.username
            textViewEmail.text = user.email
            textViewCompany.text = user.company
            textViewPosition.text = user.position
        }
    }
}
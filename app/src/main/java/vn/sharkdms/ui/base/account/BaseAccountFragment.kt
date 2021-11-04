package vn.sharkdms.ui.base.account

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.data.User
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.ui.account.AccountFragmentDirections
import vn.sharkdms.ui.customer.discount.DiscountDialogFragment

open class BaseAccountFragment : Fragment(R.layout.fragment_account) {
    private val viewModel by viewModels<AccountViewModel>()
    private var userId = 1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAccountBinding.bind(view)
        binding.apply {
            iconBack.setOnClickListener {
                findNavController().navigateUp()
            }
            cardViewDiscount.setOnClickListener {
                val dialog = DiscountDialogFragment().newInstance(userId)
                dialog.show(childFragmentManager, "AccountFragment")
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.accountEvent.collect { event ->
                when (event) {
                    is AccountViewModel.AccountEvent.BindUserInfoView -> {
                        bindUserInfoView(binding, event.user)
                        userId = event.user.id
                    }
                }
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.getUserInfo(it)
        }
    }

    private fun bindUserInfoView(binding: FragmentAccountBinding, user: User) {
        binding.apply {
            Glide.with(this@BaseAccountFragment).load(user.avatar).error(R.drawable.ic_avatar)
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
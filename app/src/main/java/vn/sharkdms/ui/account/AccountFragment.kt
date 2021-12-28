package vn.sharkdms.ui.account

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.activity.SharedViewModel
import vn.sharkdms.data.User
import vn.sharkdms.databinding.FragmentAccountBinding
import vn.sharkdms.util.Utils

open class AccountFragment : Fragment(R.layout.fragment_account) {
    companion object {
        const val TAG = "AccountFragment"
    }

    private val viewModel by viewModels<AccountViewModel>()
    open val sharedViewModel by activityViewModels<SharedViewModel>()

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
                    is AccountViewModel.AccountEvent.BindUserInfoView -> {
                        bindUserInfoView(binding, event.user)
                    }
                }
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.getUserInfo(it)
        }
        setFragmentResultListener(ImageChooserDialogFragment.UPLOAD_AVATAR) { _, bundle ->
            val imageUrl = bundle.getString(ImageChooserDialogFragment.IMAGE_URL)
            if (imageUrl.isNullOrEmpty()) return@setFragmentResultListener
            sharedViewModel.customerAvatar.value = imageUrl
            Glide.with(requireContext()).load(imageUrl).error(R.drawable.avatar_create_customer)
                .into(binding.imageViewAvatar)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun bindUserInfoView(binding: FragmentAccountBinding, user: User) {
        binding.apply {
            Glide.with(this@AccountFragment).load(user.avatar).error(R.drawable.avatar_create_customer)
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
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
import vn.sharkdms.ui.base.account.AccountViewModel
import vn.sharkdms.ui.base.account.BaseAccountFragment
import vn.sharkdms.ui.customer.discount.DiscountDialogFragment

@AndroidEntryPoint
class AccountFragment : BaseAccountFragment() {
}
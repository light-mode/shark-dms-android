package vn.sharkdms.ui.cart.details

import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.ui.customer.list.Customer

@AndroidEntryPoint
class CartDetailsFragmentCustomer : CartDetailsFragment() {

    private val args by navArgs<CartDetailsFragmentCustomerArgs>()

    override fun getCartFromArgs() = args.cart

    override fun getCustomerFromArgs(): Customer? = null
}
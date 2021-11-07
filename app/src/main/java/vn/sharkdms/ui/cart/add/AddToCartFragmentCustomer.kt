package vn.sharkdms.ui.cart.add

import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddToCartFragmentCustomer : AddToCartFragment() {

    private val args by navArgs<AddToCartFragmentCustomerArgs>()

    override fun getProductFromArgs() = args.product

    override fun getCustomerFromArgs() = args.customer
}
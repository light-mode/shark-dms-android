package vn.sharkdms.ui.cart.details

import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartDetailsFragmentSale : CartDetailsFragment() {

    private val args by navArgs<CartDetailsFragmentSaleArgs>()

    override fun getCartFromArgs() = args.cart

    override fun getCustomerFromArgs() = args.customer
}
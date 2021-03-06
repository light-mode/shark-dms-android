package vn.sharkdms.ui.product

import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragmentSale : ProductsFragment() {

    private val args by navArgs<ProductsFragmentSaleArgs>()

    override fun getCustomerFromArgs() = args.customer
}
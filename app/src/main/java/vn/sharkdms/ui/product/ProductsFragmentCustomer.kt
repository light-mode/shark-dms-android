package vn.sharkdms.ui.product

import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.api.Customer

@AndroidEntryPoint
class ProductsFragmentCustomer : ProductsFragment() {

    override fun getCustomerFromArgs(): Customer? = null
}
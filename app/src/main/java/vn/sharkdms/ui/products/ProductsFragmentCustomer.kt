package vn.sharkdms.ui.products

import dagger.hilt.android.AndroidEntryPoint
import vn.sharkdms.ui.customer.list.Customer

@AndroidEntryPoint
class ProductsFragmentCustomer : ProductsFragment() {

    override fun getCustomerFromArgs(): Customer? = null
}
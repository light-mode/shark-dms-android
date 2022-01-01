package vn.sharkdms.ui.product

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.Customer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val baseApi: BaseApi) {
    fun getSearchResults(token: String, name: String, customer: Customer?) =
        Pager(config = PagingConfig(pageSize = 20, maxSize = 60, enablePlaceholders = false),
            pagingSourceFactory = { ProductPagingSource(baseApi, token, name, customer) }).liveData
}
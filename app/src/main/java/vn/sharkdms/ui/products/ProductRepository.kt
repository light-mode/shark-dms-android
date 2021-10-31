package vn.sharkdms.ui.products

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import vn.sharkdms.api.BaseApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val baseApi: BaseApi) {
    fun getSearchResults(token: String, name: String) =
        Pager(config = PagingConfig(pageSize = 20, maxSize = 60, enablePlaceholders = false),
            pagingSourceFactory = { ProductPagingSource(baseApi, token, name) }).liveData
}
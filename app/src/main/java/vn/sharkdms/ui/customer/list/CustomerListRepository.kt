package vn.sharkdms.ui.customer.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import vn.sharkdms.api.BaseApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerListRepository @Inject constructor(private val baseApi: BaseApi) {
    fun getListData(token: String, customerName: String) =
        Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { CustomerPagingSource(baseApi, token, customerName) }).liveData
}
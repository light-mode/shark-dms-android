package vn.sharkdms.ui.history.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import vn.sharkdms.api.BaseApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryOrderListRepositoryCustomer @Inject constructor(private val baseApi: BaseApi) {
    fun getListData(token: String, date: String) =
        Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { HistoryOrderPagingSourceCustomer(baseApi, token, date) }).liveData
}
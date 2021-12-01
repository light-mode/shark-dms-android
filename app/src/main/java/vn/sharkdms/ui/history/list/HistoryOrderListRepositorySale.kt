package vn.sharkdms.ui.history.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import vn.sharkdms.api.BaseApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryOrderListRepositorySale @Inject constructor(private val baseApi: BaseApi) {
    fun getListData(token: String, customerName: String, date: String) =
        Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { HistoryOrderPagingSourceSale(baseApi, token, customerName, date) }).liveData
}
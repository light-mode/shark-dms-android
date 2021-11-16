package vn.sharkdms.ui.base.history.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import vn.sharkdms.api.BaseApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryOrderListRepository @Inject constructor(private val baseApi: BaseApi) {
    fun getListData(token: String, customerName: String, date: String) =
        Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { HistoryOrderPagingSource(baseApi, token, customerName, date) }).liveData
}
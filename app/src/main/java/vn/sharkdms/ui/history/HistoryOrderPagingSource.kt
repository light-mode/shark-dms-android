package vn.sharkdms.ui.history

import androidx.paging.PagingSource
import androidx.paging.PagingState
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.HistoryOrderListRequest
import java.lang.Exception

class HistoryOrderPagingSource(val apiService: BaseApi, val token: String, val customerName: String, val date: String): PagingSource<Int, HistoryOrder>() {
    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, HistoryOrder>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryOrder> {
        return try {
            val nextPage: Int = params.key ?: FIRST_PAGE_INDEX
            val body = HistoryOrderListRequest(nextPage, customerName, date)
            val response = apiService.getHistoryOrder(token, body)

            return LoadResult.Page(
                data = response.data,
                prevKey = if (nextPage == FIRST_PAGE_INDEX) null else nextPage - 1,
                nextKey = nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
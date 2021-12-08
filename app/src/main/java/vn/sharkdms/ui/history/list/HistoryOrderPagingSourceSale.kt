package vn.sharkdms.ui.history.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.HistoryOrderListRequest
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.HttpStatus
import java.io.IOException

class HistoryOrderPagingSourceSale(private val apiService: BaseApi, val token: String, val customerName: String, val date: String): PagingSource<Int, HistoryOrder>() {
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

            if (response.code == HttpStatus.UNAUTHORIZED.toString()) throw UnauthorizedException()

            return LoadResult.Page(
                data = response.data,
                prevKey = if (nextPage == FIRST_PAGE_INDEX) null else nextPage - 1,
                nextKey = nextPage + 1
            )
        } catch (ioe: IOException) {
            LoadResult.Error(ioe)
        } catch (he: HttpException) {
            LoadResult.Error(he)
        } catch (ue: UnauthorizedException) {
            LoadResult.Error(ue)
        }
    }

}
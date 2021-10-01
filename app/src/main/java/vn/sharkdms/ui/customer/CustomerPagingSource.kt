package vn.sharkdms.ui.customer

import androidx.activity.viewModels
import androidx.paging.PagingSource
import androidx.paging.PagingState
import vn.sharkdms.SharedViewModel
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.CustomerListRequest
import java.lang.Exception
import java.security.Key

class CustomerPagingSource(val apiService: BaseApi, val token: String, val customerName: String): PagingSource<Int, Customer>() {
    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Customer>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Customer> {
        return try {
            val nextPage: Int = params.key ?: FIRST_PAGE_INDEX
            val body = CustomerListRequest(nextPage, customerName)
            val response = apiService.listCustomer(token, body)

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
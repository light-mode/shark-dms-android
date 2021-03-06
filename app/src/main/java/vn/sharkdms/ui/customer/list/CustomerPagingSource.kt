package vn.sharkdms.ui.customer.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.Customer
import vn.sharkdms.api.CustomerListRequest
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.HttpStatus
import java.io.IOException

class CustomerPagingSource(private val apiService: BaseApi, val token: String, val customerName: String): PagingSource<Int, Customer>() {
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
package vn.sharkdms.ui.products

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.ProductListRequest
import vn.sharkdms.util.Constant
import java.io.IOException

class ProductPagingSource(private val baseApi: BaseApi, private val token: String,
    private val name: String) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val position = params.key ?: 1
        val authorization = Constant.TOKEN_PREFIX + token
        val body = ProductListRequest(position.toString(), name)
        return try {
            val response = baseApi.getProductsSale(authorization, body)
            val products = response.data
            LoadResult.Page(products, if (position == 1) null else position - 1,
                if (products.isEmpty()) null else position + 1)
        } catch (ioe: IOException) {
            LoadResult.Error(ioe)
        } catch (he: HttpException) {
            LoadResult.Error(he)
        }
    }
}
package vn.sharkdms.ui.product

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.ProductListRequest
import vn.sharkdms.ui.customer.list.Customer
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import java.io.IOException

class ProductPagingSource(private val baseApi: BaseApi, private val token: String,
    private val name: String, private val customer: Customer?) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val position = params.key ?: 1
        val authorization = Constant.TOKEN_PREFIX + token
        return try {
            val response = if (customer == null) baseApi.getProductsCustomer(authorization,
                position, name) else baseApi.getProductsSale(authorization,
                ProductListRequest(position.toString(), name))
            if (response.code.toInt() == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
            val products = response.data
            LoadResult.Page(products, if (position == 1) null else position - 1,
                if (products.isEmpty()) null else position + 1)
        } catch (ioe: IOException) {
            LoadResult.Error(ioe)
        } catch (he: HttpException) {
            LoadResult.Error(he)
        } catch (ue: UnauthorizedException) {
            LoadResult.Error(ue)
        }
    }
}
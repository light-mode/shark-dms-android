package vn.sharkdms.api

import retrofit2.http.*
import vn.sharkdms.ui.cart.Cart

interface BranchApi {
    companion object {
        const val BRANCH_URL = "https://shop.gtechvn.org/api/"
        private const val AUTHORIZATION = "Authorization"
    }

    @POST("customer/login")
    suspend fun login(
        @Body body: LoginRequest
    ): BaseResponse<LoginResponse>

    @POST("customer/cart/delete")
    suspend fun removeFromCartCustomer(
        @Header(AUTHORIZATION) authorization: String,
        @Body body: RemoveFromCartRequest
    ): BaseResponse<Cart?>
}
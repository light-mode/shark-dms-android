package vn.sharkdms.api

import retrofit2.http.Body
import retrofit2.http.POST

interface BaseApi {
    companion object {
        const val BASE_URL = "http://be.sharkdms.vn/api/"
    }

    @POST("forgot-password")
    suspend fun forgotPassword(
        @Body forgotPasswordRequest: ForgotPasswordRequest): BaseResponse<Nothing>

    @POST("customer/login")
    suspend fun login(@Body body: LoginRequest): BaseResponse<LoginResponseData>
}
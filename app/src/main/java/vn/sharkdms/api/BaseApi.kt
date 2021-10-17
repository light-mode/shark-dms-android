package vn.sharkdms.api

import retrofit2.http.*
import vn.sharkdms.ui.customer.Customer
import vn.sharkdms.ui.overview.Amount
import vn.sharkdms.ui.tasks.Task

interface BaseApi {
    companion object {
        const val BASE_URL = "https://be.sharkdms.vn/api/"
        private const val AUTHORIZATION = "Authorization"
    }

    /** User API
     *
     */
    @POST("forgot-password")
    suspend fun forgotPassword(
        @Body forgotPasswordRequest: ForgotPasswordRequest): BaseResponse<Nothing>

    @POST("customer/login")
    suspend fun login(@Body body: LoginRequest): BaseResponse<LoginResponseData>

    @POST("change-password")
    suspend fun changePassword(@Header("Authorization") authorization: String,
        @Body body: ChangePasswordRequest) : BaseResponse<Nothing>

    @POST("staff/task")
    suspend fun listTask(@Header("Authorization") authorization: String,
    @Body body: TaskListRequest) : BaseResponse<List<Task>>

    @POST("staff/edit-status-task")
    suspend fun updateTaskStatus(@Header(AUTHORIZATION) authorization: String,
        @Body body: UpdateTaskStatusRequest): BaseResponse<UpdateTaskStatusResponseData?>

    @GET("amount")
    suspend fun getAmounts(@Header(AUTHORIZATION) authorization: String): BaseResponse<List<Amount>>

    @GET("get-report")
    suspend fun getReport(
        @Header(AUTHORIZATION) authorization: String): BaseResponse<Any>

    @POST("create-report")
    suspend fun createReport(@Header(AUTHORIZATION) authorization: String,
        @Body body: CreateReportRequest): BaseResponse<Nothing>

    @POST("edit-report")
    suspend fun editReport(@Header(AUTHORIZATION) authorization: String,
        @Body body: UpdateReportRequest): BaseResponse<Nothing>

    /** Customer API
     *
     */
    @POST("list-customer")
    suspend fun listCustomer(
        @Header("Authorization") token: String,
        @Body customerListRequest: CustomerListRequest): BaseResponse<List<Customer>>

}
package vn.sharkdms.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import vn.sharkdms.ui.customer.create.CreateCustomerAccount
import vn.sharkdms.ui.customer.discount.DiscountInfo
import vn.sharkdms.ui.customer.list.Customer
import vn.sharkdms.ui.history.HistoryOrder
import vn.sharkdms.ui.notifications.Notification
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

    @POST("notification")
    suspend fun getNotifications(@Header(AUTHORIZATION) authorization: String,
        @Body body: NotificationListRequest): BaseResponse<List<Notification>>

    /** Customer API
     *
     */
    @POST("list-customer")
    suspend fun listCustomer(
        @Header(AUTHORIZATION) token: String,
        @Body customerListRequest: CustomerListRequest): BaseResponse<List<Customer>>

    @Multipart
    @POST("create-customer")
    suspend fun createCustomer(
        @Header(AUTHORIZATION) token: String,
        @Part("name") name: RequestBody,
        @Part("account") account: RequestBody,
        @Part("password") password: RequestBody,
        @Part("address") address: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("long") long: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("email") email: RequestBody,
        @Part image: List<MultipartBody.Part>?): BaseResponse<CreateCustomerAccount>

    @GET("config/discount/{id}")
    suspend fun getDiscount(
        @Header(AUTHORIZATION) authorization: String,
        @Path("id") id: Int?): BaseResponse<List<DiscountInfo>>

    @POST("checkin-customer")
    suspend fun checkInCustomer(
        @Header(AUTHORIZATION) authorization: String,
        @Body checkInRequest: CheckInRequest): BaseResponse<Nothing>

    @Multipart
    @POST("create-gallery")
    suspend fun uploadGallery(
        @Header(AUTHORIZATION) token: String,
        @Part("user_kh_id") userKhId: RequestBody,
        @Part("address") address: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("long") long: RequestBody,
        @Part image: List<MultipartBody.Part>?): BaseResponse<Nothing>

    /** History Order API
     *
     */
    @POST("list-history-order")
    suspend fun getHistoryOrder(
        @Header(AUTHORIZATION) token: String,
        @Body body: HistoryOrderListRequest): BaseResponse<List<HistoryOrder>>
}
package vn.sharkdms.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import vn.sharkdms.ui.base.history.info.OrderDetail
import vn.sharkdms.ui.base.history.list.HistoryOrder
import vn.sharkdms.ui.cart.Cart
import vn.sharkdms.ui.customer.create.CreateCustomerAccount
import vn.sharkdms.ui.customer.discount.DiscountInfo
import vn.sharkdms.ui.customer.list.Customer
import vn.sharkdms.ui.notifications.Notification
import vn.sharkdms.ui.overview.Amount
import vn.sharkdms.ui.products.Product
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

    @POST("list-product-npp")
    suspend fun getProductsSale(@Header(AUTHORIZATION) authorization: String,
        @Body body: ProductListRequest): BaseResponse<List<Product>>

    @GET("customer/product")
    suspend fun getProductsCustomer(@Header(AUTHORIZATION) authorization: String,
        @Query("page") page: Int,
        @Query("product_name") productName: String): BaseResponse<List<Product>>

    @POST("add-to-cart")
    suspend fun addToCart(@Header(AUTHORIZATION) authorization: String,
        @Body body: AddToCartRequest): BaseResponse<Cart>

    @POST("customer/cart")
    suspend fun addToCartCustomer(@Header(AUTHORIZATION) authorization: String,
        @Body body: AddToCartRequest): BaseResponse<Cart>

    @POST("delete-item-cart")
    suspend fun removeFromCart(@Header(AUTHORIZATION) authorization: String,
        @Body body: RemoveFromCartRequest): BaseResponse<Cart?>

    @HTTP(method = "DELETE", path = "customer/cart", hasBody = true)
    suspend fun removeFromCartCustomer(@Header(AUTHORIZATION) authorization: String,
        @Body body: RemoveFromCartRequest): BaseResponse<Cart?>

    @POST("delete-cart")
    suspend fun deleteCart(@Header(AUTHORIZATION) authorization: String,
        @Body body: DeleteCartRequest): BaseResponse<Nothing>

    @POST("create-order")
    suspend fun createOrder(@Header(AUTHORIZATION) authorization: String,
        @Body body: CreateOrderRequest): BaseResponse<CreateOrderResponse>

    @POST("customer/order")
    suspend fun createOrderCustomer(@Header(AUTHORIZATION) authorization: String,
        @Body body: CreateOrderRequest): BaseResponse<CreateOrderResponse>

    @GET("customer/cart/{id}")
    suspend fun getCartInfoAsCustomer(@Header(AUTHORIZATION) authorization: String,
        @Path("id") id: Int): BaseResponse<Cart>

    @POST("replace-avatar")
    suspend fun uploadAvatar(@Header(AUTHORIZATION) authorization: String,
        @Body body: RequestBody): BaseResponse<UploadAvatarResponse>

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
        @Part image: MultipartBody.Part?): BaseResponse<CreateCustomerAccount>

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
        @Part("address") address: RequestBody?,
        @Part("lat") lat: RequestBody?,
        @Part("long") long: RequestBody?,
        @Part image: List<MultipartBody.Part>?): BaseResponse<Nothing>

    /** History Order API
     *
     */
    @POST("list-history-order")
    suspend fun getHistoryOrder(
        @Header(AUTHORIZATION) token: String,
        @Body body: HistoryOrderListRequest): BaseResponse<List<HistoryOrder>>

    @GET("customer/order")
    suspend fun getCustomerHistoryOrder(
        @Header(AUTHORIZATION) token: String,
        @Query("page") page: Int,
        @Query("date") date: String): BaseResponse<List<HistoryOrder>>

    @POST("list-history-order-detail")
    suspend fun getOrderInfo(
        @Header(AUTHORIZATION) token: String,
        @Body orderDetailRequest: OrderDetailRequest): BaseResponse<OrderDetail>

    @GET("customer/order/{id}")
    suspend fun getCustomerHistoryOrderDetail(
        @Header(AUTHORIZATION) token: String,
        @Path("id") id: Int?): BaseResponse<OrderDetail>
}
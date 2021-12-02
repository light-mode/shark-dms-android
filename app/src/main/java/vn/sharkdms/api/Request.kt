package vn.sharkdms.api

import com.google.gson.annotations.SerializedName

data class AddToCartRequest(
    val customerId: String,
    val productId: String,
    @SerializedName("product_qty") val productQuantity: String
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

data class CheckInRequest(
    val userKhId: Int,
    var address: String?,
    var lat: String,
    var long: String,
    var image: String
)

data class CreateOrderRequest(
    @SerializedName("quote_id") val cartId: String,
    val discount: String,
    val note: String,
    val address: String
)

data class CreateReportRequest(
    val title: String,
    val description: String
)

data class CustomerListRequest(
    val page: Int,
    val customerName: String
)

data class DeleteCartRequest(
    @SerializedName("quote_id") val cartId: String
)

data class ForgotPasswordRequest(
    val account: String
)

data class HistoryOrderListRequest(
    val page: Int,
    val customerName: String,
    val date: String
)

data class LoginRequest(
    val account: String,
    val password: String
)

data class NotificationListRequest(
    val page: String
)

data class OrderDetailRequest(
    val orderId: String
)

data class ProductListRequest(
    val page: String,
    val productName: String
)

data class RemoveFromCartRequest(
    @SerializedName("quote_id") val cartId: String,
    @SerializedName("quote_item_id") val cartItemId: String
)

data class TaskListRequest(
    val page: String,
    val searchDate: String
)

data class UpdateReportRequest(
    val reportId: Int,
    val title: String,
    val description: String
)

data class UpdateTaskStatusRequest(
    val taskId: String,
    val status: Int
)
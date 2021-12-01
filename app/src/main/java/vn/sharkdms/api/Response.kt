package vn.sharkdms.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import vn.sharkdms.ui.cart.CartItem

data class BaseResponse<T>(
    val code: String,
    val status: String,
    val message: String,
    val data: T,
    val totalPage: Int
)

@Parcelize
data class CreateOrderResponse(
    @SerializedName("user_name_kh", alternate = ["customer_name"]) val customerName: String,
    val discount: String,
    val note: String,
    val totalAmount: Long,
    @SerializedName("created_at") val orderTimestamp: String,
    val orderCode: String,
    @SerializedName("item", alternate = ["order_items"]) val items: List<CartItem>
) : Parcelable

data class GetReportResponse(
    val id: Int,
    val title: String,
    val description: String,
    val userId: Int,
    val createdAt: String,
    val updatedAt: String
)

data class LoginResponse(
    val token: String,
    val id: Int,
    val name: String,
    val phone: String,
    val avatar: String,
    val createdAt: String,
    val email: String,
    val position: String,
    val company: String,
    @SerializedName("roleId") val roleId: Int,
    @SerializedName("roleName") val roleName: String
)

data class UpdateTaskStatusResponse(
    val id: Int,
    val taskName: String,
    val taskDescription: String,
    val userAssignedId: Int,
    val userAssignId: Int,
    val status: Int,
    val createdAt: String,
    val updatedAt: String,
    val userCompanyId: Int
)

data class UploadAvatarResponse(
    val image: String?
)

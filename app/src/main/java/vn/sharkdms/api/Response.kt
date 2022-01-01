package vn.sharkdms.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class BaseResponse<T>(
    val code: String,
    val status: String,
    val message: String,
    val data: T,
    val totalPage: Int
)

@Parcelize
data class Cart(
    @SerializedName("quote_id") val id: Int,
    val createdAt: String,
    val totalAmount: Long,
    val discountAmount: Long,
    val totalPaymentAmount: Long,
    @SerializedName("product") val items: List<CartItem>
) : Parcelable

@Parcelize
data class CartItem(
    @SerializedName("quote_item_id") val id: Int,
    @SerializedName("product_name") val name: String,
    @SerializedName("product_sku") val sku: String,
    @SerializedName("qty") val quantity: Long,
    @SerializedName("product_price") val price: Long,
    val amount: Long,
    val totalPrice: Long?
) : Parcelable

data class CreateCustomerAccount(
    val account: String,
    val password: String
)

@Parcelize
data class DiscountInfo(
    @SerializedName("ruleCode") var ruleCode: String,
    @SerializedName("ruleContent") var ruleContent: String,
    @SerializedName("minAmount") var minAmount: Double,
    @SerializedName("maxAmount") var maxAmount: Double,
    @SerializedName("discountRate") var discountRate: Double
) : Parcelable

@Parcelize
data class Customer (
    val stt: Int,
    val customerId: Int,
    val customerName: String,
    var customerAvatar: String,
    var customerAddress: String?,
    var customerPosition: String,
    var customerPhone: String,
    val customerEmail: String?,
    var status: String,
    var rankName: String,
    var checkInDate: String
) : Parcelable

data class OrderDetail(
    val orderCode: String,
    val customerName: String,
    val customerPhone: String,
    val totalAmount: Double,
    val status: String,
    val note: String,
    val discount: Double,
    val createdAt: String,
    val orderItems: List<OrderItem>
)

data class OrderItem(
    val productSku: String,
    val productName: String,
    val totalPrice: Double,
    val qty: Int,
    val currency: String
)

@Parcelize
data class HistoryOrder(
    val stt: Int,
    val orderId: Int,
    val orderCode: String,
    val orderCustomerId: String,
    val customerName: String,
    val customerPhone: String,
    val orderTotalAmount: Double,
    val orderStatus: String,
    val orderDate: String
) : Parcelable, Comparable<HistoryOrder> {
    override fun compareTo(other: HistoryOrder): Int {
        return this.stt.compareTo(other.stt)
    }
}

@Parcelize
data class Notification(
    @SerializedName("id_noti") val id: Int,
    @SerializedName("title_noti") val title: String,
    @SerializedName("content_noti") val content: String,
    val date: String
) : Parcelable

data class Amount(
    @SerializedName("revuene") var revenue: String,
    val month: Int,
    val year: Int,
    val id: Int
)

@Parcelize
data class Product(@SerializedName("stt") val index: Int,
    @SerializedName("product_npp_code") val id: Int,
    @SerializedName("product_product_name") val name: String,
    @SerializedName("product_product_code") val code: String,
    @SerializedName("product_category_id") val categoryId: Int,
    @SerializedName("product_sku") val sku: String,
    @SerializedName("product_medias") val imageUrl: String,
    @SerializedName("product_price") val price: Long,
    @SerializedName("product_qty") val quantity: Long,
    @SerializedName("npp_id") val supplierId: Int,
    @SerializedName("product_status") val status: String,
    @SerializedName("product_price_discount") val discounts: List<Discount>) : Parcelable

@Parcelize
data class Discount(
    @SerializedName("number_product_min") val min: Long,
    @SerializedName("number_product_max") val max: Long,
    @SerializedName("discount_price") val value: Long
) : Parcelable

@Parcelize
data class Task(
    val id: Int,
    val taskName: String,
    val taskDescription: String,
    val status: Int
) : Parcelable

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

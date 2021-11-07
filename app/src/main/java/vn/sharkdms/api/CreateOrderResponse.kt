package vn.sharkdms.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import vn.sharkdms.ui.cart.CartItem

@Parcelize
data class CreateOrderResponse(
    @SerializedName("user_name_kh", alternate = ["customer_name"]) val customerName: String,
    val discount: String, val note: String, val totalAmount: Long,
    @SerializedName("created_at") val orderTimestamp: String, val orderCode: String,
    @SerializedName("item", alternate = ["order_items"]) val items: List<CartItem>) : Parcelable

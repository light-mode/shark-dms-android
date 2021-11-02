package vn.sharkdms.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import vn.sharkdms.ui.cart.CartItem

@Parcelize
data class CreateOrderResponse(@SerializedName("user_name_kh") val customerName: String,
    val discount: String, val note: String, val totalAmount: Long, val status: Int,
    @SerializedName("created_at") val orderTimestamp: String, val orderCode: String,
    @SerializedName("item") val items: List<CartItem>) : Parcelable

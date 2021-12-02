package vn.sharkdms.ui.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart(@SerializedName("quote_id") val id: Int, val createdAt: String,
    val totalAmount: Long, val discountAmount: Long, val totalPaymentAmount: Long,
    @SerializedName("product") val items: List<CartItem>) : Parcelable



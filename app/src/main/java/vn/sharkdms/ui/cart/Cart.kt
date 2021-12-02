package vn.sharkdms.ui.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart(@SerializedName("quote_id") val id: Int, val createdAt: String,
    val totalAmount: Long, val discountAmount: Long, val totalPaymentAmount: Long,
    @SerializedName("product") val items: List<CartItem>) : Parcelable

@Parcelize
data class CartItem(@SerializedName("quote_item_id") val id: Int,
                    @SerializedName("product_name") val name: String,
                    @SerializedName("product_sku") val sku: String, @SerializedName("qty") val quantity: Long,
                    @SerializedName("product_price") val price: Long, val amount: Long,
                    val totalPrice: Long?) : Parcelable

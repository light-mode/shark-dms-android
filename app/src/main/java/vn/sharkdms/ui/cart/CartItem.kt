package vn.sharkdms.ui.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(@SerializedName("quote_item_id") val id: Int,
                    @SerializedName("product_name") val name: String,
                    @SerializedName("product_sku") val sku: String, @SerializedName("qty") val quantity: Long,
                    @SerializedName("product_price") val price: Long, val amount: Long,
                    val totalPrice: Long?) : Parcelable
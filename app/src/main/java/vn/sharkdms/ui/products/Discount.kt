package vn.sharkdms.ui.products

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Discount(@SerializedName("number_product_min") val min: Long,
                    @SerializedName("number_product_max") val max: Long,
                    @SerializedName("discount_price") val value: Long) : Parcelable
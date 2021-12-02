package vn.sharkdms.ui.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

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
data class Discount(@SerializedName("number_product_min") val min: Long,
                    @SerializedName("number_product_max") val max: Long,
                    @SerializedName("discount_price") val value: Long) : Parcelable



package vn.sharkdms.api

import com.google.gson.annotations.SerializedName

data class AddToCartRequest(val customerId: String, val productId: String,
    @SerializedName("product_qty") val productQuantity: String)
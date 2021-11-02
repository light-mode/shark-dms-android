package vn.sharkdms.api

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(@SerializedName("quote_id") val cartId: String, val discount: String,
    val note: String, val address: String)

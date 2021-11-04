package vn.sharkdms.api

import com.google.gson.annotations.SerializedName

data class RemoveFromCartRequest(@SerializedName("quote_id") val cartId: String,
    @SerializedName("quote_item_id") val cartItemId: String)

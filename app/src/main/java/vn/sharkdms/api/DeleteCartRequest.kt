package vn.sharkdms.api

import com.google.gson.annotations.SerializedName

data class DeleteCartRequest(@SerializedName("quote_id") val cartId: String)

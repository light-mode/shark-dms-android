package vn.sharkdms.ui.customer.discount

import com.google.gson.annotations.SerializedName

data class DiscountInfo(
    @SerializedName("ruleCode") var ruleCode: String,
    @SerializedName("ruleContent") var ruleContent: String,
    @SerializedName("minAmount") var minAmount: Int,
    @SerializedName("maxAmount") var maxAmount: Int,
    @SerializedName("discountRate") var discountRate: Int
)
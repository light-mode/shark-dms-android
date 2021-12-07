package vn.sharkdms.ui.customer.discount

import com.google.gson.annotations.SerializedName

data class DiscountInfo(
    @SerializedName("ruleCode") var ruleCode: String,
    @SerializedName("ruleContent") var ruleContent: String,
    @SerializedName("minAmount") var minAmount: Double,
    @SerializedName("maxAmount") var maxAmount: Double,
    @SerializedName("discountRate") var discountRate: Double
)
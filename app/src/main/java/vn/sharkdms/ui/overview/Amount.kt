package vn.sharkdms.ui.overview

import com.google.gson.annotations.SerializedName

data class Amount(@SerializedName("revuene") var revenue: String, val month: Int, val year: Int,
    val id: Int)

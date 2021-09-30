package vn.sharkdms.api

data class CustomerListRequest(
    val page: Int,
    val customerName: String
)
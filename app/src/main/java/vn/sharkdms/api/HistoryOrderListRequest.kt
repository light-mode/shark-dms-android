package vn.sharkdms.api

data class HistoryOrderListRequest(
    val page: Int,
    val customerName: String,
    val date: String
)
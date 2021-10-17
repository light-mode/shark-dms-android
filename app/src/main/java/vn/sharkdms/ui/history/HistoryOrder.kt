package vn.sharkdms.ui.history

data class HistoryOrder(
    val stt: Int,
    val orderId: Int,
    val orderCode: String,
    val orderCustomerId: String,
    val customerName: String,
    val customerPhone: String,
    val orderTotalAmount: Double,
    val orderStatus: String,
    val orderDate: String
)
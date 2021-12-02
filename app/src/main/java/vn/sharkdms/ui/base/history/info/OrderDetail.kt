package vn.sharkdms.ui.base.history.info

data class OrderDetail(
    val orderCode: String,
    val customerName: String,
    val customerPhone: String,
    val totalAmount: Double,
    val status: String,
    val note: String,
    val discount: Double,
    val createdAt: String,
    val orderItems: List<OrderItem>
)
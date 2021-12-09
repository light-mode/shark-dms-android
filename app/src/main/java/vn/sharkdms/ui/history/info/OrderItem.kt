package vn.sharkdms.ui.history.info

data class OrderItem(
    val productSku: String,
    val productName: String,
    val totalPrice: Double,
    val qty: Int,
    val currency: String
)
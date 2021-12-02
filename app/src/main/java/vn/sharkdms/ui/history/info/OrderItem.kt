package vn.sharkdms.ui.base.history.info

data class OrderItem(
    val productSku: String,
    val productName: String,
    val totalPrice: Double,
    val qty: Int,
    val currency: String
)
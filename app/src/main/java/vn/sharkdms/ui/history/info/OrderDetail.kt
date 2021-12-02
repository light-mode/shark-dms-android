package vn.sharkdms.ui.history.info

import vn.sharkdms.ui.base.history.info.OrderItem

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
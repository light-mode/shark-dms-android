package vn.sharkdms.ui.customer

import java.util.*

data class Customer (
    val stt: Int,
    val customerId: Int,
    val customerName: String,
    var customerAvatar: String,
    var customerAddress: String,
    var customerPosition: String,
    var customerPhone: String,
    var customerEmail: String,
    var status: String,
    var rankName: String,
    var checkInDate: String
        )
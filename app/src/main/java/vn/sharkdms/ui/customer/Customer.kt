package vn.sharkdms.ui.customer

import java.util.*

data class CustomerList (
    val customers: List<Customer>
        )

data class Customer (
    val stt: Int,
    val id: Int,
    val name: String,
    var avatar: String,
    var address: String,
    var position: String,
    var phoneNumber: String,
    var email: String,
    var status: String,
    var rank: String,
    var checkIn: Date
        )
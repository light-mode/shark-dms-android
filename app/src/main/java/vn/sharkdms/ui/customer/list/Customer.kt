package vn.sharkdms.ui.customer.list

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
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
        ) : Parcelable
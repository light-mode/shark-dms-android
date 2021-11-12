package vn.sharkdms.ui.customer.list

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.Comparator

@Parcelize
data class Customer (
    val stt: Int,
    val customerId: Int,
    val customerName: String,
    var customerAvatar: String,
    var customerAddress: String?,
    var customerPosition: String,
    var customerPhone: String,
    val customerEmail: String?,
    var status: String,
    var rankName: String,
    var checkInDate: String
) : Parcelable
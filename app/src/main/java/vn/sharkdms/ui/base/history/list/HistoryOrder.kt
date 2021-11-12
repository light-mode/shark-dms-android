package vn.sharkdms.ui.base.history.list

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Parcelize
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
) : Parcelable, Comparable<HistoryOrder> {
    override fun compareTo(other: HistoryOrder): Int {
        return this.stt.compareTo(other.stt)
    }
}
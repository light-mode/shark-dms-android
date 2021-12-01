package vn.sharkdms.ui.notification.list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(@SerializedName("id_noti") val id: Int,
    @SerializedName("title_noti") val title: String,
    @SerializedName("content_noti") val content: String, val date: String) : Parcelable

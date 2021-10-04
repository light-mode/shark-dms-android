package vn.sharkdms.ui.tasks

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(val id: Int, val taskName: String, val taskDescription: String,
    val status: Int) : Parcelable

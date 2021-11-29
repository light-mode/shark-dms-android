package vn.sharkdms.ui.customer.avatar

import android.graphics.Bitmap
import android.net.Uri

interface OnPhotoSelectedListener {
    fun getImagePath(imageUri: Uri?)
    fun getImageBitmap(imageBitmap: Bitmap?)
}
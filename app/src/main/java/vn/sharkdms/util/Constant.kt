package vn.sharkdms.util

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import vn.sharkdms.R

class Constant {
    companion object {
        const val ROLE_ID_SR = 7
        const val ROLE_ID_KH = 8

        const val TASK_STATUS_NEW = 0
        const val TASK_STATUS_COMPLETED = 1
        const val TASK_STATUS_PROCESSING = 2
        const val TASK_STATUS_CHECKING = 3
        const val TASK_STATUS_NOT_COMPLETED = 4

        const val ADDRESS_LIMIT = 30
        const val PRODUCT_LIMIT = 25

        const val TOKEN_PREFIX = "Bearer "

        const val CUSTOMER_DETAIL_RANK = "Xếp loại: "
        const val CUSTOMER_DETAIL_ADDRESS = "Địa chỉ: "
        const val CUSTOMER_DETAIL_CHECK_IN = "Ngày check-in: "

        const val ORDER_STATUS_NEW = "Mới"
        const val ORDER_STATUS_PROCESSING_QUERY = "Đang xử lí"
        const val ORDER_STATUS_PROCESSING = "Đang xử lý"
        const val ORDER_STATUS_DONE = "Hoàn thành"
        const val ORDER_STATUS_CANCEL_QUERY = "Huỷ"
        const val ORDER_STATUS_CANCEL = "Hủy"
        const val ORDER_PRODUCT_AMOUNT = " sản phẩm"

        fun collapseDisplay(text: String?, limit: Int): String {
            if (text == null) return ""
            else if (text.length <= limit) return text
            else {
                return text.substring(0, limit).plus("...")
            }
        }

        fun hideSoftKeyboard(activity: AppCompatActivity) {
            val inputMethodManager: InputMethodManager =
                activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isAcceptingText) {
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus?.windowToken,
                    0
                )
            }
        }

        fun setupUI(view: View, activity: AppCompatActivity) {
            if (view !is EditText) {
                view.setOnTouchListener(object: View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        hideSoftKeyboard(activity)
                        return false
                    }
                })
            }

            if (view is ViewGroup) {
                for (i in 0..view.childCount) {
                    if (view.getChildAt(i) != null) {
                        val innerView: View = view.getChildAt(i)
                        if (innerView.id == R.id.tv_date_picker) continue
                        setupUI(innerView, activity)
                    }
                }
            }
        }
    }
}
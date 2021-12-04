package vn.sharkdms.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import vn.sharkdms.R
import vn.sharkdms.ui.logout.UnauthorizedDialogFragment

class Utils {
    companion object {
        fun showUnauthorizedDialog(activity: FragmentActivity) {
            UnauthorizedDialogFragment().show(activity.supportFragmentManager, "")
        }

        fun hideSoftKeyboard(activity: AppCompatActivity) {
            val inputMethodManager: InputMethodManager =
                activity.getSystemService(
                    AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isAcceptingText) {
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus?.windowToken,
                    0
                )
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun setupUI(view: View, activity: AppCompatActivity) {
            if (view !is EditText) {
                view.setOnTouchListener { _, _ ->
                    hideSoftKeyboard(activity)
                    false
                }
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

        fun showConnectivityOffMessage(context: Context) {
            Toast.makeText(context, context.getString(R.string.message_connectivity_off),
                Toast.LENGTH_SHORT).show()
        }
    }
}
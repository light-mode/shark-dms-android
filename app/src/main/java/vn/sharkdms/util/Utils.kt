package vn.sharkdms.util

import androidx.fragment.app.FragmentActivity
import vn.sharkdms.ui.logout.UnauthorizedDialog

class Utils {
    companion object {
        fun showUnauthorizedDialog(activity: FragmentActivity) {
            UnauthorizedDialog().show(activity.supportFragmentManager, "")
        }
    }
}
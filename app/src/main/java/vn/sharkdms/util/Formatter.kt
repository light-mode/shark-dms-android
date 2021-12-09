package vn.sharkdms.util

import android.content.Context
import vn.sharkdms.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class Formatter {
    companion object {
        fun formatCurrency(currency: String): String {
            val dfs = DecimalFormatSymbols()
            dfs.groupingSeparator = ' '
            return DecimalFormat("#,###", dfs).format(currency.toBigDecimal())
        }

        fun formatDate(context: Context, date: String): String {
            val dmy = date.split("-").toTypedArray()
            val year = dmy[2]
            val month = dmy[1]
            val day = dmy[0]
            val calendar = Calendar.getInstance()
            calendar.set(year.toInt(), month.toInt() - 1, day.toInt())
            val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> context.getString(R.string.sunday)
                Calendar.MONDAY -> context.getString(R.string.monday)
                Calendar.TUESDAY -> context.getString(R.string.tuesday)
                Calendar.WEDNESDAY -> context.getString(R.string.wednesday)
                Calendar.THURSDAY -> context.getString(R.string.thursday)
                Calendar.FRIDAY -> context.getString(R.string.friday)
                Calendar.SATURDAY -> context.getString(R.string.saturday)
                else -> ""
            }
            return "$dayOfWeek, $day/$month/$year"
        }

        fun collapseDisplay(text: String?, limit: Int): String {
            return when {
                text == null -> ""
                text.length <= limit -> text
                else -> text.substring(0, limit).plus("...")
            }
        }
    }
}
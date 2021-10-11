package vn.sharkdms.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class Formatter {
    companion object {
        fun formatCurrency(currency: String): String {
            val dfs = DecimalFormatSymbols()
            dfs.groupingSeparator = ' '
            return DecimalFormat("#,###", dfs).format(currency.toBigDecimal())
        }
    }
}
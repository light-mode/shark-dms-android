package vn.sharkdms.ui.history.list

import vn.sharkdms.api.HistoryOrder

sealed class HistoryOrderUiModel {
    data class HeaderItem(val text: String) : HistoryOrderUiModel()
    data class HistoryOrderItem(val historyOrder: HistoryOrder) : HistoryOrderUiModel()
}
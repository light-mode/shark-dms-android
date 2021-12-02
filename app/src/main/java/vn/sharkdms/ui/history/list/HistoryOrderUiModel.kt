package vn.sharkdms.ui.history.list

sealed class HistoryOrderUiModel {
    data class HeaderItem(val text: String) : HistoryOrderUiModel()
    data class HistoryOrderItem(val historyOrder: HistoryOrder) : HistoryOrderUiModel()
}
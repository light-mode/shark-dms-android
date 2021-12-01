package vn.sharkdms.ui.notifications

import vn.sharkdms.ui.history.list.HistoryOrder

sealed class UiModel {
    data class NotificationItem(val notification: Notification) : UiModel()
    data class HeaderItem(val text: String) : UiModel()
    data class HistoryOrderItem(val historyOrder: HistoryOrder) : UiModel()
}

package vn.sharkdms.ui.notifications

sealed class UiModel {
    data class NotificationItem(val notification: Notification) : UiModel()
    data class HeaderItem(val text: String) : UiModel()
}

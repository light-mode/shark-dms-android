package vn.sharkdms.ui.notification.list

sealed class NotificationUiModel {
    data class NotificationItem(val notification: Notification) : NotificationUiModel()
    data class HeaderItem(val text: String) : NotificationUiModel()
}

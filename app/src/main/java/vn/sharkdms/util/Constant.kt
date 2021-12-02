package vn.sharkdms.util

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

        const val ORDER_STATUS_NEW = "Mới"
        const val ORDER_STATUS_PROCESSING_QUERY = "Đang xử lí"
        const val ORDER_STATUS_PROCESSING = "Đang xử lý"
        const val ORDER_STATUS_DONE = "Hoàn thành"
        const val ORDER_STATUS_CANCEL_QUERY = "Huỷ"
        const val ORDER_STATUS_CANCEL = "Hủy"
        const val ORDER_STATUS_STOCK_OUT = "Đơn hàng xuất kho"
    }
}
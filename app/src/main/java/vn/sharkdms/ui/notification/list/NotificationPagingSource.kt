package vn.sharkdms.ui.notification.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.NotificationListRequest
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import java.io.IOException

class NotificationPagingSource(private val baseApi: BaseApi,
    private val token: String) : PagingSource<Int, Notification>() {

    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
        val position = params.key ?: 1
        val authorization = Constant.TOKEN_PREFIX + token
        val body = NotificationListRequest(position.toString())
        return try {
            val response = baseApi.getNotifications(authorization, body)
            if (response.code.toInt() == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
            val notifications = response.data
            LoadResult.Page(notifications, if (position == 1) null else position - 1,
                if (notifications.isEmpty()) null else position + 1)
        } catch (ioe: IOException) {
            LoadResult.Error(ioe)
        } catch (he: HttpException) {
            LoadResult.Error(he)
        } catch (ue: UnauthorizedException) {
            LoadResult.Error(ue)
        }
    }
}
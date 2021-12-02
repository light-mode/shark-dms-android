package vn.sharkdms.ui.notification.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import vn.sharkdms.api.BaseApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(private val baseApi: BaseApi) {
    fun getSearchResults(token: String) =
        Pager(config = PagingConfig(pageSize = 20, maxSize = 60, enablePlaceholders = false),
            pagingSourceFactory = { NotificationPagingSource(baseApi, token) }).liveData
}
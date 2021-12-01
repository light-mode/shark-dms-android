package vn.sharkdms.ui.task.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import vn.sharkdms.api.BaseApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val baseApi: BaseApi) {
    fun getSearchResults(token: String, searchDate: String) =
        Pager(config = PagingConfig(pageSize = 20, maxSize = 60, enablePlaceholders = false),
            pagingSourceFactory = { TaskPagingSource(baseApi, token, searchDate) }).liveData
}
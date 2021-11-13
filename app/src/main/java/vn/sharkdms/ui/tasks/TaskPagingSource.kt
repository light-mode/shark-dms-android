package vn.sharkdms.ui.tasks

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.TaskListRequest
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import java.io.IOException

class TaskPagingSource(private val baseApi: BaseApi, private val token: String,
    private val searchDate: String) : PagingSource<Int, Task>() {

    override fun getRefreshKey(state: PagingState<Int, Task>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Task> {
        val position = params.key ?: 1
        val authorization = Constant.TOKEN_PREFIX + token
        val body = TaskListRequest(position.toString(), searchDate)
        return try {
            val response = baseApi.listTask(authorization, body)
            if (response.code == HttpStatus.UNAUTHORIZED.toString()) throw UnauthorizedException()
            val tasks = response.data
            LoadResult.Page(tasks, if (position == 1) null else position - 1,
                if (tasks.isEmpty()) null else position + 1)
        } catch (ioe: IOException) {
            LoadResult.Error(ioe)
        } catch (he: HttpException) {
            LoadResult.Error(he)
        } catch (ue: UnauthorizedException) {
            LoadResult.Error(ue)
        }
    }
}
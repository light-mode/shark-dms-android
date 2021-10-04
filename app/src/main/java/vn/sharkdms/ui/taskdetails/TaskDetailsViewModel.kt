package vn.sharkdms.ui.taskdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.UpdateTaskStatusRequest
import vn.sharkdms.util.Constant
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    private val taskDetailsEventChannel = Channel<TaskDetailsEvent>()
    val taskDetailsEvent = taskDetailsEventChannel.receiveAsFlow()
    val statusSelectorShowing = MutableLiveData(false)

    fun updateTaskStatus(token: String, taskId: Int, status: Int) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            val body = UpdateTaskStatusRequest(taskId.toString(), status)
            try {
                val response = baseApi.updateTaskStatus(authorization, body)
                if (response.data == null) {
                    taskDetailsEventChannel.send(TaskDetailsEvent.OnError(response.message))
                } else {
                    taskDetailsEventChannel.send(TaskDetailsEvent.OnSuccess(response.message))
                }
            } catch (ske: SocketTimeoutException) {
                taskDetailsEventChannel.send(TaskDetailsEvent.OnFailure)
            }
        }
    }

    sealed class TaskDetailsEvent {
        data class OnSuccess(val message: String) : TaskDetailsEvent()
        data class OnError(val message: String) : TaskDetailsEvent()
        object OnFailure : TaskDetailsEvent()
    }
}
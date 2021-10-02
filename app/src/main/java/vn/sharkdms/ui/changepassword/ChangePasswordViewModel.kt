package vn.sharkdms.ui.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.ChangePasswordRequest
import vn.sharkdms.data.UserDao
import vn.sharkdms.util.Constant
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val baseApi: BaseApi,
    private val userDao: UserDao) : ViewModel() {

    private val changePasswordEventChannel = Channel<ChangePasswordEvent>()
    val changePasswordEvent = changePasswordEventChannel.receiveAsFlow()

    fun sendChangePasswordRequest(token: String, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            val body = ChangePasswordRequest(oldPassword, newPassword)
            try {
                val response = baseApi.changePassword(authorization, body)
                changePasswordEventChannel.send(
                    ChangePasswordEvent.OnResponse(response.code.toInt(), response.message))
            } catch (ste: SocketTimeoutException) {
                changePasswordEventChannel.send(ChangePasswordEvent.OnFailure)
            }
        }
    }

    fun deleteUserInfo() {
        viewModelScope.launch {
            userDao.deleteUserInfo()
            changePasswordEventChannel.send(ChangePasswordEvent.ShowLoginScreen)
        }
    }

    sealed class ChangePasswordEvent {
        data class OnResponse(val code: Int, val message: String) : ChangePasswordEvent()
        object OnFailure : ChangePasswordEvent()
        object ShowLoginScreen : ChangePasswordEvent()
    }
}
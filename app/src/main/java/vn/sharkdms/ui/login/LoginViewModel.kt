package vn.sharkdms.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.LoginRequest
import vn.sharkdms.api.LoginResponseData
import vn.sharkdms.data.User
import vn.sharkdms.data.UserDao
import vn.sharkdms.util.Constant
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val baseApi: BaseApi,
    private val userDao: UserDao) : ViewModel() {

    private val loginEventChannel = Channel<LoginEvent>()
    val loginEvent = loginEventChannel.receiveAsFlow()

    fun sendLoginRequest(username: String, password: String) {
        viewModelScope.launch {
            val body = LoginRequest(username, password)
            try {
                val response = baseApi.login(body)
                loginEventChannel.send(
                    LoginEvent.OnResponse(response.code, response.message, response.data))
            } catch (ste: SocketTimeoutException) {
                loginEventChannel.send(LoginEvent.OnFailure)
            }
        }
    }

    fun saveUserInfo(data: LoginResponseData) {
        viewModelScope.launch {
            data.apply {
                val user = User(token, id, name, phone, avatar, createdAt, email, position, company,
                    roleId, roleName, 1)
                userDao.insert(user)
                when (roleId) {
                    Constant.ROLE_ID_SR -> loginEventChannel.send(
                        LoginEvent.ShowOverviewScreen(token, name, roleName))
                    Constant.ROLE_ID_KH -> loginEventChannel.send(
                        LoginEvent.ShowProductsScreen(token, name, roleName))
                }
            }
        }
    }

    sealed class LoginEvent {
        data class OnResponse(val code: String, val message: String,
            val data: LoginResponseData?) : LoginEvent()

        object OnFailure : LoginEvent()
        data class ShowOverviewScreen(val token: String, val username: String,
            val roleName: String) : LoginEvent()

        data class ShowProductsScreen(val token: String, val username: String,
            val roleName: String) : LoginEvent()
    }
}
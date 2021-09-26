package vn.sharkdms.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.LoginRequest
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    private val loginEventChannel = Channel<LoginEvent>()
    val loginEvent = loginEventChannel.receiveAsFlow()

    fun sendLoginRequest(username: String, password: String) {
        viewModelScope.launch {
            val body = LoginRequest(username, password)
            try {
                val response = baseApi.login(body)
                loginEventChannel.send(LoginEvent.OnResponse(response.code, response.message))
            } catch (ste: SocketTimeoutException) {
                loginEventChannel.send(LoginEvent.OnFailure)
            }
        }
    }

    sealed class LoginEvent {
        data class OnResponse(val code: String, val message: String) : LoginEvent()
        object OnFailure : LoginEvent()
    }
}
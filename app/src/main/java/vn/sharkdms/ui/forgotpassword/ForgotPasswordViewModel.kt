package vn.sharkdms.ui.forgotpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.ForgotPasswordRequest
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {
    companion object {
        const val TAG = "ForgotPasswordViewModel"
    }

    private val forgotPasswordEventChannel = Channel<ForgotPasswordEvent>()
    val forgotPasswordEvent = forgotPasswordEventChannel.receiveAsFlow()

    fun sendForgotPasswordRequest(username: String) {
        viewModelScope.launch {
            val body = ForgotPasswordRequest(username)
            try {
                val response = baseApi.forgotPassword(body)
                val code = response.code.toInt()
                forgotPasswordEventChannel.send(
                    ForgotPasswordEvent.OnResponse(code, response.message))
            } catch (nfe: NumberFormatException) {
                Log.e(TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                forgotPasswordEventChannel.send(ForgotPasswordEvent.OnFailure)
            }
        }
    }

    sealed class ForgotPasswordEvent {
        data class OnResponse(val code: Int, val message: String) : ForgotPasswordEvent()
        object OnFailure : ForgotPasswordEvent()
    }
}
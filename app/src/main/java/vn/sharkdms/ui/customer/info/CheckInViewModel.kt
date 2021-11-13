package vn.sharkdms.ui.customer.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.CheckInRequest
import vn.sharkdms.ui.customer.create.CreateCustomerViewModel
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.HttpStatus
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    companion object {
        const val TAG = "CheckInViewModel"
    }

    private val checkInEventChannel = Channel<CheckInEvent>()
    val checkInEvent = checkInEventChannel.receiveAsFlow()

    fun checkInRequest(authorization: String, checkInRequest: CheckInRequest) {
        viewModelScope.launch {
            try {
                val response = baseApi.checkInCustomer(authorization, checkInRequest)
                val code = response.code.toInt()
                if (code == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                checkInEventChannel.send(
                    CheckInEvent.OnResponse(code, response.message)
                )
            } catch (nfe: NumberFormatException) {
                Log.e(TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                checkInEventChannel.send(CheckInEvent.OnFailure)
            } catch (ue: UnauthorizedException) {
                checkInEventChannel.send(CheckInEvent.ShowUnauthorizedDialog)
            }
        }
    }

    sealed class CheckInEvent {
        data class OnResponse(val code: Int, val message: String): CheckInEvent()
        object OnFailure : CheckInEvent()
        object ShowUnauthorizedDialog : CheckInEvent()
    }
}
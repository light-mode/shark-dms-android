package vn.sharkdms.ui.base.history.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.OrderDetailRequest
import vn.sharkdms.util.HttpStatus
import java.lang.Exception
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    companion object {
        const val TAG = "OrderDetailViewModel"
    }

    private val orderDetailEventChannel = Channel<OrderDetailEvent>()
    val orderDetailEvent = orderDetailEventChannel.receiveAsFlow()

    fun getOrderDetail(authorization: String, orderDetailRequest: OrderDetailRequest) {
        viewModelScope.launch {
            try {
                val response = baseApi.getOrderInfo(authorization, orderDetailRequest)
                val code = response.code.toInt()
                if (code == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                orderDetailEventChannel.send(
                    OrderDetailEvent.OnResponse(code, response.message, response.data)
                )
            } catch (nfe: NumberFormatException) {
                Log.e(TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                orderDetailEventChannel.send(OrderDetailEvent.OnFailure)
            } catch (ue: UnauthorizedException) {
                orderDetailEventChannel.send(OrderDetailEvent.ShowUnauthorizedDialog)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }
        }
    }

    sealed class OrderDetailEvent {
        data class OnResponse(val code: Int, val message: String, val data: OrderDetail?): OrderDetailEvent()
        object OnFailure: OrderDetailEvent()
        object ShowUnauthorizedDialog: OrderDetailEvent()
    }
}
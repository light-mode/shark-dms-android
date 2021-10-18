package vn.sharkdms.ui.history.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    companion object {
        const val TAG = "OrderDetailViewModel"
    }

    private val orderDetailEventChannel = Channel<OrderDetailEvent>()
    private val orderDetailEvent = orderDetailEventChannel.receiveAsFlow()

    fun getOrderDetail(authorization: String, orderId: Int) {
        viewModelScope.launch {
            try {
                val response = baseApi.getOrderInfo(authorization, orderId)
                val code = response.code.toInt()
                orderDetailEventChannel.send(
                    OrderDetailEvent.OnResponse(code, response.message, response.data)
                )
            } catch (nfe: NumberFormatException) {
                Log.e(TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                orderDetailEventChannel.send(OrderDetailEvent.OnFailure)
            }
        }
    }

    sealed class OrderDetailEvent {
        data class OnResponse(val code: Int, val message: String, val data: OrderDetail?): OrderDetailEvent()
        object OnFailure: OrderDetailEvent()
    }
}
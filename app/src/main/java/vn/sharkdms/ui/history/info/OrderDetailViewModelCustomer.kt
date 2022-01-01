package vn.sharkdms.ui.history.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.OrderDetail
import vn.sharkdms.util.HttpStatus
import java.lang.Exception
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModelCustomer @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    private val orderDetailEventChannel = Channel<OrderDetailEvent>()
    val orderDetailEvent = orderDetailEventChannel.receiveAsFlow()

    fun getOrderDetail(authorization: String, orderId: Int?) {
        viewModelScope.launch {
            try {
                val response = baseApi.getCustomerHistoryOrderDetail(authorization, orderId)
                val code = response.code.toInt()
                if (code == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                orderDetailEventChannel.send(
                    OrderDetailEvent.OnResponse(code, response.message, response.data)
                )
            } catch (nfe: NumberFormatException) {
                Log.e(OrderDetailViewModelSale.TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                orderDetailEventChannel.send(OrderDetailEvent.OnFailure)
            } catch (ue: Exception) {
                orderDetailEventChannel.send(OrderDetailEvent.ShowUnauthorizedDialog)
            } catch (e: Exception) {
                Log.e(OrderDetailViewModelSale.TAG, e.message, e)
            }
        }
    }

    sealed class OrderDetailEvent {
        data class OnResponse(val code: Int, val message: String, val data: OrderDetail?): OrderDetailEvent()
        object OnFailure: OrderDetailEvent()
        object ShowUnauthorizedDialog: OrderDetailEvent()
    }
}
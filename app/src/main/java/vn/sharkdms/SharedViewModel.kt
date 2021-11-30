package vn.sharkdms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.ui.cart.Cart
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {
    val connectivity = MutableLiveData<Boolean>()
    var token = ""

    private val customerEventChannel = Channel<CustomerEvent>()
    val customerEvent = customerEventChannel.receiveAsFlow()
    val cartId = MutableLiveData<Int>()
    val customerAvatar = MutableLiveData<String>()

    fun getCartInfoAsCustomer() {
        val authorization = Constant.TOKEN_PREFIX + token
        val cartId = cartId.value
        viewModelScope.launch {
            try {
                if (cartId == null || cartId == 0) {
                    customerEventChannel.send(CustomerEvent.NavigateToCartInfoScreen(null))
                    return@launch
                }
                val response = baseApi.getCartInfoAsCustomer(authorization, cartId)
                if (response.code.toInt() == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                customerEventChannel.send(CustomerEvent.NavigateToCartInfoScreen(response.data))
            } catch (ste: SocketTimeoutException) {
                customerEventChannel.send(CustomerEvent.ShowNetworkConnectionErrorMessage)
            } catch (ue: UnauthorizedException) {
                customerEventChannel.send(CustomerEvent.ShowUnauthorizedDialog)
            }
        }
    }

    sealed class CustomerEvent {
        data class NavigateToCartInfoScreen(val cart: Cart?) : CustomerEvent()
        object ShowNetworkConnectionErrorMessage : CustomerEvent()
        object ShowUnauthorizedDialog : CustomerEvent()
    }
}
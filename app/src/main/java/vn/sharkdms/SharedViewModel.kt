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
import vn.sharkdms.util.Constant
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {
    val connectivity = MutableLiveData<Boolean>()
    var token = ""

    private val customerEventChannel = Channel<CustomerEvent>()
    val customerEvent = customerEventChannel.receiveAsFlow()
    val cartId = MutableLiveData<Int>()

    fun getCartInfoAsCustomer() {
        val authorization = Constant.TOKEN_PREFIX + token
        val cartId = cartId.value
        if (cartId == null || cartId == 0) return
        viewModelScope.launch {
            try {
                val response = baseApi.getCartInfoAsCustomer(authorization, cartId)
                customerEventChannel.send(CustomerEvent.NavigateToCartInfoScreen(response.data))
            } catch (ste: SocketTimeoutException) {
                customerEventChannel.send(CustomerEvent.ShowNetworkConnectionErrorMessage)
            }
        }
    }

    sealed class CustomerEvent {
        data class NavigateToCartInfoScreen(val cart: Cart) : CustomerEvent()
        object ShowNetworkConnectionErrorMessage : CustomerEvent()
    }
}
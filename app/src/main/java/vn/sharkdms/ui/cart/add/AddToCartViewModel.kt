package vn.sharkdms.ui.cart.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.AddToCartRequest
import vn.sharkdms.api.BaseApi
import vn.sharkdms.ui.cart.Cart
import vn.sharkdms.util.Constant
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class AddToCartViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    private val addToCartEventChannel = Channel<AddToCartEvent>()
    val addToCartEvent = addToCartEventChannel.receiveAsFlow()
    val currentQuantity = MutableLiveData(0L)
    val currentTotalPrice = MutableLiveData(0L)

    fun minusOneToQuantity(price: Long) {
        val quantity = currentQuantity.value ?: return
        if (quantity == 0L) return
        currentQuantity.value = quantity - 1
        currentTotalPrice.value = (quantity - 1) * price
    }

    fun plusOneToQuantity(price: Long, remain: Long) {
        val quantity = currentQuantity.value ?: return
        if (quantity == remain) return
        currentQuantity.value = quantity + 1
        currentTotalPrice.value = (quantity + 1) * price
    }

    fun addToCart(token: String, customerId: Int, productId: Int) {
        val authorization = Constant.TOKEN_PREFIX + token
        val body = AddToCartRequest(customerId.toString(), productId.toString(),
            currentQuantity.value.toString())
        viewModelScope.launch {
            try {
                val response = baseApi.addToCart(authorization, body)
                addToCartEventChannel.send(
                    AddToCartEvent.OnResponse(response.message, response.data))
            } catch (ste: SocketTimeoutException) {
                addToCartEventChannel.send(AddToCartEvent.OnFailure)
            }
        }
    }

    sealed class AddToCartEvent {
        data class OnResponse(val message: String, val cart: Cart?) : AddToCartEvent()
        object OnFailure : AddToCartEvent()
    }
}
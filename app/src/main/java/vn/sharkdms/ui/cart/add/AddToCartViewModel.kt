package vn.sharkdms.ui.cart.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.api.AddToCartRequest
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.Cart
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
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
        viewModelScope.launch {
            addToCartEventChannel.send(
                AddToCartEvent.UpdateQuantityEditText((quantity - 1).toString()))
        }
    }

    fun plusOneToQuantity(price: Long, remain: Long) {
        val quantity = currentQuantity.value ?: return
        if (quantity == remain) return
        currentQuantity.value = quantity + 1
        currentTotalPrice.value = (quantity + 1) * price
        viewModelScope.launch {
            addToCartEventChannel.send(
                AddToCartEvent.UpdateQuantityEditText((quantity + 1).toString()))
        }
    }

    fun setQuantity(text: String, price: Long, remain: Long) {
        var quantity = 0L
        if (text.isEmpty()) {
            viewModelScope.launch {
                addToCartEventChannel.send(AddToCartEvent.UpdateQuantityEditText("0"))
            }
        } else quantity = text.toLong()
        if (quantity > remain) {
            quantity = remain
            viewModelScope.launch {
                addToCartEventChannel.send(AddToCartEvent.UpdateQuantityEditText(remain.toString()))
            }
        }
        currentQuantity.value = quantity
        currentTotalPrice.value = quantity * price
    }

    fun addToCart(token: String, customerId: Int, productId: Int) {
        val authorization = Constant.TOKEN_PREFIX + token
        val body = AddToCartRequest(customerId.toString(), productId.toString(),
            currentQuantity.value.toString())
        viewModelScope.launch {
            try {
                val response = if (customerId == 0) baseApi.addToCartCustomer(authorization, body)
                else baseApi.addToCart(authorization, body)
                if (response.code.toInt() == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                addToCartEventChannel.send(
                    AddToCartEvent.OnResponse(response.message, response.data))
            } catch (ste: SocketTimeoutException) {
                addToCartEventChannel.send(AddToCartEvent.OnFailure)
            } catch (ue: UnauthorizedException) {
                addToCartEventChannel.send(AddToCartEvent.ShowUnauthorizedDialog)
            }
        }
    }

    sealed class AddToCartEvent {
        data class UpdateQuantityEditText(val quantity: String) : AddToCartEvent()
        data class OnResponse(val message: String, val cart: Cart?) : AddToCartEvent()
        object OnFailure : AddToCartEvent()
        object ShowUnauthorizedDialog : AddToCartEvent()
    }
}
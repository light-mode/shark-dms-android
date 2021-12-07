package vn.sharkdms.ui.cart.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.*
import vn.sharkdms.ui.cart.Cart
import vn.sharkdms.ui.customer.list.Customer
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class CartDetailsViewModel @Inject constructor(private val baseApi: BaseApi, private val branchApi: BranchApi) : ViewModel() {

    private val cartDetailsEventChannel = Channel<CartDetailsEvent>()
    val cartDetailsEvent = cartDetailsEventChannel.receiveAsFlow()
    var cartItemId: Int? = null
    var cart: Cart? = null
    var removeLastItem = false

    fun removeFromCart(token: String, customer: Customer?) {
        if (cart == null || cartItemId == null) return
        val authorization = Constant.TOKEN_PREFIX + token
        val body = RemoveFromCartRequest(cart!!.id.toString(), cartItemId.toString())
        viewModelScope.launch {
            try {
                val response = if (customer == null) branchApi.removeFromCartCustomer(authorization,
                    body) else baseApi.removeFromCart(authorization, body)
                val message = response.message
                when (response.code.toInt()) {
                    HttpStatus.OK -> {
                        cart = response.data
                        if (cart == null) {
                            removeLastItem = true
                            cartDetailsEventChannel.send(
                                CartDetailsEvent.OnRemoveLastItemSuccess(message))
                        } else cartDetailsEventChannel.send(
                            CartDetailsEvent.OnRemoveItemSuccess(message, cart!!))
                    }
                    HttpStatus.BAD_REQUEST -> cartDetailsEventChannel.send(
                        CartDetailsEvent.ShowBadRequestErrorMessage(message))
                    HttpStatus.UNAUTHORIZED -> cartDetailsEventChannel.send(
                        CartDetailsEvent.ShowUnauthorizedDialog)
                }
            } catch (ste: SocketTimeoutException) {
                cartDetailsEventChannel.send(CartDetailsEvent.ShowNetworkConnectionErrorMessage)
            }
        }
    }

    fun cancelOrder(token: String) {
        if (cart == null) return
        val authorization = Constant.TOKEN_PREFIX + token
        val body = DeleteCartRequest(cart!!.id.toString())
        viewModelScope.launch {
            try {
                val response = baseApi.deleteCart(authorization, body)
                when (response.code.toInt()) {
                    HttpStatus.OK -> cartDetailsEventChannel.send(
                        CartDetailsEvent.OnCancelOrderSuccess(response.message))
                    HttpStatus.BAD_REQUEST -> cartDetailsEventChannel.send(
                        CartDetailsEvent.ShowBadRequestErrorMessage(response.message))
                    HttpStatus.UNAUTHORIZED -> cartDetailsEventChannel.send(
                        CartDetailsEvent.ShowUnauthorizedDialog)
                }
            } catch (ste: SocketTimeoutException) {
                cartDetailsEventChannel.send(CartDetailsEvent.ShowNetworkConnectionErrorMessage)
            }
        }
    }

    fun createOrder(token: String, discount: String, note: String, customer: Customer?) {
        if (cart == null) return
        val authorization = Constant.TOKEN_PREFIX + token
        val body = CreateOrderRequest(cart!!.id.toString(), discount, note, "")
        viewModelScope.launch {
            try {
                val response = if (customer == null) baseApi.createOrderCustomer(authorization,
                    body)
                else baseApi.createOrder(authorization, body)
                val message = response.message
                when (response.code.toInt()) {
                    HttpStatus.OK -> cartDetailsEventChannel.send(
                        CartDetailsEvent.OnCreateOrderSuccess(response.data))
                    HttpStatus.BAD_REQUEST -> cartDetailsEventChannel.send(
                        CartDetailsEvent.ShowBadRequestErrorMessage(message))
                    HttpStatus.UNAUTHORIZED -> cartDetailsEventChannel.send(
                        CartDetailsEvent.ShowUnauthorizedDialog)
                }
            } catch (ste: SocketTimeoutException) {
                cartDetailsEventChannel.send(CartDetailsEvent.ShowNetworkConnectionErrorMessage)
            }
        }
    }

    sealed class CartDetailsEvent {
        data class OnRemoveItemSuccess(val message: String, val cart: Cart) : CartDetailsEvent()
        data class OnRemoveLastItemSuccess(val message: String) : CartDetailsEvent()
        data class OnCancelOrderSuccess(val message: String) : CartDetailsEvent()
        data class OnCreateOrderSuccess(val data: CreateOrderResponse) : CartDetailsEvent()
        data class ShowBadRequestErrorMessage(val message: String) : CartDetailsEvent()
        object ShowNetworkConnectionErrorMessage : CartDetailsEvent()
        object ShowUnauthorizedDialog : CartDetailsEvent()
    }
}
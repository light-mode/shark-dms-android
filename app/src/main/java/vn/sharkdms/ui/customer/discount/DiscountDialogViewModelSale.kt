package vn.sharkdms.ui.customer.discount

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.DiscountInfo
import vn.sharkdms.ui.customer.create.CreateCustomerViewModel
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class DiscountDialogViewModelSale @Inject constructor(private val baseApi: BaseApi) : ViewModel(){

    private val discountDialogEventChannel = Channel<DiscountDialogEvent>()
    val discountDialogEvent = discountDialogEventChannel.receiveAsFlow()

    fun sendGetCompanyDiscountInfo(token: String) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            try {
                val response = baseApi.getCompanyDiscountSale(authorization)
                val code = response.code.toInt()
                if (code == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                discountDialogEventChannel.send(DiscountDialogEvent.OnResponse(code, response.message, response.data))
            } catch (nfe: NumberFormatException) {
                Log.e(CreateCustomerViewModel.TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                discountDialogEventChannel.send(DiscountDialogEvent.OnFailure)
            } catch (ue: UnauthorizedException) {
                discountDialogEventChannel.send(DiscountDialogEvent.ShowUnauthorizedDialog)
            }
        }
    }

    fun sendGetEachCustomerDiscountInfo(token: String, id: Int?) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            try {
                val response = baseApi.getEachCustomerDiscountSale(authorization, id)
                val code = response.code.toInt()
                if (code == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                discountDialogEventChannel.send(DiscountDialogEvent.OnResponse(code, response.message, response.data))
            } catch (nfe: NumberFormatException) {
                Log.e(CreateCustomerViewModel.TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                discountDialogEventChannel.send(DiscountDialogEvent.OnFailure)
            } catch (ue: UnauthorizedException) {
                discountDialogEventChannel.send(DiscountDialogEvent.ShowUnauthorizedDialog)
            }
        }
    }

    sealed class DiscountDialogEvent {
        data class OnResponse(val code: Int, val message: String, val data: List<DiscountInfo>?) : DiscountDialogEvent()
        object OnFailure : DiscountDialogEvent()
        object ShowUnauthorizedDialog : DiscountDialogEvent()
    }
}
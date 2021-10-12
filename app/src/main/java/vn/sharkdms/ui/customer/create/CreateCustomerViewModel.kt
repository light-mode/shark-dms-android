package vn.sharkdms.ui.customer.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.sharkdms.api.BaseApi
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class CreateCustomerViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    companion object {
        const val TAG = "CreateCustomerViewModel"
    }

    private val createCustomerEventChannel = Channel<CreateCustomerEvent>()
    val createCustomerEvent = createCustomerEventChannel.receiveAsFlow()

    fun sendCreateCustomerRequest(authorization: String, name: RequestBody, account: RequestBody, password: RequestBody, address: RequestBody,
            lat: RequestBody, long: RequestBody, phone: RequestBody, email: RequestBody, image: List<MultipartBody.Part>?) {
        viewModelScope.launch {
            try {
                val response = baseApi.createCustomer(authorization, name, account, password,
                    address, lat, long, phone, email, image)
                val code = response.code.toInt()
                createCustomerEventChannel.send(
                    CreateCustomerEvent.OnResponse(code, response.status, response.message, response.data))
            } catch (nfe: NumberFormatException) {
                Log.e(TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                createCustomerEventChannel.send(CreateCustomerEvent.OnFailure)
            }
        }
    }

    sealed class CreateCustomerEvent {
        data class OnResponse(val code: Int, val status: String?, val message: String, val data: CreateCustomerAccount?) : CreateCustomerEvent()
        object OnFailure : CreateCustomerEvent()
    }
}
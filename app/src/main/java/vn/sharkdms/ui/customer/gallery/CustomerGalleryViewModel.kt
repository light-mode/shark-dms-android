package vn.sharkdms.ui.customer.gallery

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
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.HttpStatus
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class CustomerGalleryViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    companion object {
        const val TAG = "GalleryViewModel"
    }

    private val customerGalleryEventChannel = Channel<CustomerGalleryEvent>()
    val customerGalleryEvent = customerGalleryEventChannel.receiveAsFlow()

    fun uploadGalleryRequest(authorization: String, requestBody: RequestBody) {
        viewModelScope.launch {
            try {
                val response = baseApi.uploadGallery(authorization, requestBody)
                val code = response.code.toInt()
                if (code == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                customerGalleryEventChannel.send(
                    CustomerGalleryEvent.OnResponse(code, response.message))
            } catch (nfe: NumberFormatException) {
                Log.e(TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                customerGalleryEventChannel.send(CustomerGalleryEvent.OnFailure)
            } catch (ue: UnauthorizedException) {
                customerGalleryEventChannel.send(CustomerGalleryEvent.ShowUnauthorizedDialog)
            }
        }
    }

    sealed class CustomerGalleryEvent {
        data class OnResponse(val code: Int, val message: String): CustomerGalleryEvent()
        object OnFailure : CustomerGalleryEvent()
        object ShowUnauthorizedDialog : CustomerGalleryEvent()
    }
}
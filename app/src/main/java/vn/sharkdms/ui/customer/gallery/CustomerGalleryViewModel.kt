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

    fun uploadGalleryRequest(authorization: String, id: RequestBody, address: RequestBody,
            lat: RequestBody, long: RequestBody, image: List<MultipartBody.Part>?) {
        viewModelScope.launch {
            try {
                val response = baseApi.uploadGallery(authorization, id, address, lat, long, image)
                val code = response.code.toInt()
                customerGalleryEventChannel.send(
                    CustomerGalleryEvent.OnResponse(code, response.message))
            } catch (nfe: NumberFormatException) {
                Log.e(TAG, nfe.message, nfe)
            } catch (ste: SocketTimeoutException) {
                customerGalleryEventChannel.send(CustomerGalleryEvent.OnFailure)
            }
        }
    }

    sealed class CustomerGalleryEvent {
        data class OnResponse(val code: Int, val message: String): CustomerGalleryEvent()
        object OnFailure : CustomerGalleryEvent()
    }
}
package vn.sharkdms.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.util.Constant
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    private val overviewEventChannel = Channel<OverviewEvent>()
    val overviewEvent = overviewEventChannel.receiveAsFlow()

    fun sendGetAmountsRequest(token: String) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            try {
                val response = baseApi.getAmounts(authorization)
                overviewEventChannel.send(OverviewEvent.OnResponse(response.data))
            } catch (ste: SocketTimeoutException) {
                overviewEventChannel.send(OverviewEvent.OnFailure)
            }
        }
    }

    sealed class OverviewEvent {
        data class OnResponse(val amounts: List<Amount>?) : OverviewEvent()
        object OnFailure : OverviewEvent()
    }
}
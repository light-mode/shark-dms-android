package vn.sharkdms.ui.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.Amount
import vn.sharkdms.api.BaseApi
import vn.sharkdms.data.User
import vn.sharkdms.data.UserDao
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(private val baseApi: BaseApi,
    userDao: UserDao) : ViewModel() {

    private val overviewEventChannel = Channel<OverviewEvent>()
    val overviewEvent = overviewEventChannel.receiveAsFlow()
    val users = userDao.getUsers().asLiveData()
    val selectedColumnIndex = MutableLiveData(2)

    fun sendGetAmountsRequest(token: String) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            try {
                val response = baseApi.getAmounts(authorization)
                if (response.code.toInt() == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                overviewEventChannel.send(OverviewEvent.OnResponse(response.data))
            } catch (ste: SocketTimeoutException) {
                overviewEventChannel.send(OverviewEvent.OnFailure)
            } catch (ue: UnauthorizedException) {
                overviewEventChannel.send(OverviewEvent.ShowUnauthorizedDialog)
            }
        }
    }

    fun getName(users: List<User>) {
        if (users.isEmpty()) return
        viewModelScope.launch {
            val user = users[0]
            overviewEventChannel.send(OverviewEvent.BindWelcomeView(user.name))
        }
    }

    sealed class OverviewEvent {
        data class OnResponse(val amounts: List<Amount>?) : OverviewEvent()
        object OnFailure : OverviewEvent()
        data class BindWelcomeView(val name: String) : OverviewEvent()
        object ShowUnauthorizedDialog : OverviewEvent()
    }
}
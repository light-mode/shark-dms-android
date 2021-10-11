package vn.sharkdms.ui.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.data.UserDao
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(private val userDao: UserDao) : ViewModel() {

    private val logoutEventChannel = Channel<LogoutEvent>()
    val logoutEvent = logoutEventChannel.receiveAsFlow()

    fun deleteUserInfo() {
        viewModelScope.launch {
            userDao.deleteUserInfo()
            logoutEventChannel.send(LogoutEvent.ShowLoginScreen)
        }
    }

    sealed class LogoutEvent {
        object ShowLoginScreen : LogoutEvent()
    }
}
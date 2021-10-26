package vn.sharkdms.ui.base.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.data.User
import vn.sharkdms.data.UserDao
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(userDao: UserDao) : ViewModel() {

    private val accountEventChannel = Channel<AccountEvent>()
    val accountEvent = accountEventChannel.receiveAsFlow()
    val users = userDao.getUsers().asLiveData()

    fun getUserInfo(users: List<User>) {
        viewModelScope.launch {
            val user = users[0]
            accountEventChannel.send(AccountEvent.BindUserInfoView(user))
        }
    }

    sealed class AccountEvent {
        data class BindUserInfoView(val user: User) : AccountEvent()
    }
}
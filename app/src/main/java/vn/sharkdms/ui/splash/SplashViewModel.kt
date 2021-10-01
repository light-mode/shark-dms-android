package vn.sharkdms.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.data.User
import vn.sharkdms.data.UserDao
import vn.sharkdms.util.Constant
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(userDao: UserDao) : ViewModel() {

    private val splashEventChannel = Channel<SplashEvent>()
    val splashEvent = splashEventChannel.receiveAsFlow()
    val users = userDao.getUsers().asLiveData()

    fun checkLoginStatus(users: List<User>) {
        viewModelScope.launch {
            delay(2000)
            if (users.isEmpty()) {
                splashEventChannel.send(SplashEvent.ShowLoginScreen)
                return@launch
            }
            val user = users[0]
            when (user.roleId) {
                Constant.ROLE_ID_SR -> splashEventChannel.send(
                    SplashEvent.ShowOverviewScreen(user.token, user.name, user.roleName))
                Constant.ROLE_ID_KH -> splashEventChannel.send(
                    SplashEvent.ShowProductsScreen(user.token, user.name, user.roleName))
            }
        }
    }

    sealed class SplashEvent {
        object ShowLoginScreen : SplashEvent()
        data class ShowOverviewScreen(val token: String, val username: String,
            val roleName: String) : SplashEvent()

        data class ShowProductsScreen(val token: String, val username: String,
            val roleName: String) : SplashEvent()
    }
}
package vn.sharkdms.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.sharkdms.api.BaseApi
import vn.sharkdms.data.UserDao
import vn.sharkdms.ui.logout.UnauthorizedException
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import java.io.File
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class ImageChooserViewModel @Inject constructor(private val baseApi: BaseApi,
    private val userDao: UserDao) : ViewModel() {

    private val imageChooserEventChannel = Channel<ImageChooserEvent>()
    val imageChooserEvent = imageChooserEventChannel.receiveAsFlow()
    var currentPhotoPath: String = ""
    var imageUrl: String = ""

    fun uploadAvatar(token: String) {
        val authorization = Constant.TOKEN_PREFIX + token
        val file = File(currentPhotoPath)
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart("image[]", file.name, RequestBody.create(MultipartBody.FORM, file))
        viewModelScope.launch {
            try {
                val response = baseApi.uploadAvatar(authorization, builder.build())
                if (response.code.toInt() == HttpStatus.UNAUTHORIZED) throw UnauthorizedException()
                imageUrl = response.data.image ?: ""
                updateAvatar(response.message)
            } catch (ste: SocketTimeoutException) {
                imageChooserEventChannel.send(ImageChooserEvent.OnFailure)
            } catch (ue: UnauthorizedException) {
                imageChooserEventChannel.send(ImageChooserEvent.ShowUnauthorizedDialog)
            } finally {
                file.delete()
            }
        }
    }

    private suspend fun updateAvatar(message: String) {
        if (imageUrl.isEmpty()) return
        userDao.updateAvatar(imageUrl)
        imageChooserEventChannel.send(ImageChooserEvent.OnUploadAvatarSuccess(message))
    }

    sealed class ImageChooserEvent {
        data class OnUploadAvatarSuccess(val message: String) : ImageChooserEvent()
        object OnFailure : ImageChooserEvent()
        object ShowUnauthorizedDialog : ImageChooserEvent()
    }
}
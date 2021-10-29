package vn.sharkdms.ui.notifications

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.sharkdms.util.Formatter
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(application: Application,
    private val repository: NotificationRepository) : AndroidViewModel(application) {

    fun getNotifications(token: String) = repository.getSearchResults(token).map { pagingData ->
        pagingData.map { UiModel.NotificationItem(it) }
    }.map { pagingData ->
        pagingData.insertSeparators { before, after ->
            if (after == null) return@insertSeparators null
            val context = getApplication<Application>().applicationContext
            if (before == null) return@insertSeparators UiModel.HeaderItem(
                Formatter.formatDate(context, after.notification.date))
            if (before.notification.date == after.notification.date) null
            else UiModel.HeaderItem(Formatter.formatDate(context, after.notification.date))
        }
    }.cachedIn(viewModelScope)
}
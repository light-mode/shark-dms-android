package vn.sharkdms.ui.base.history.list

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import vn.sharkdms.api.BaseApi
import vn.sharkdms.di.AppModule
import vn.sharkdms.ui.notifications.UiModel
import vn.sharkdms.util.Formatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class HistoryOrderListViewModel @Inject constructor(application: Application, private val repository: HistoryOrderListRepository)
    : AndroidViewModel(application) {
    companion object {
        const val TAG = "HistoryOrderViewModel"
    }

    fun getListData(token: String, customerName: String, date: String) =
        repository.getListData(token, customerName, date).map { pagingData -> pagingData.map { UiModel.HistoryOrderItem(it) } }
            .map { pagingData ->
                pagingData.insertSeparators { before, after ->
                    if (after == null) return@insertSeparators null
                    val context = getApplication<Application>().applicationContext
                    if (before == null) return@insertSeparators UiModel.HeaderItem(
                        Formatter.formatDate(context, after.historyOrder.orderDate)
                    )
                    if (before.historyOrder.orderDate == after.historyOrder.orderDate) null
                    else UiModel.HeaderItem(Formatter.formatDate(context, after.historyOrder.orderDate))
                }
            }.cachedIn(viewModelScope)

}
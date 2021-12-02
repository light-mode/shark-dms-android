package vn.sharkdms.ui.historycustomer.list

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import vn.sharkdms.api.BaseApi
import vn.sharkdms.di.AppModule
import vn.sharkdms.ui.base.history.list.HistoryOrder
import vn.sharkdms.ui.base.history.list.HistoryOrderAdapter
import vn.sharkdms.ui.notifications.UiModel
import vn.sharkdms.util.Formatter
import javax.inject.Inject

@HiltViewModel
class CustomerHistoryOrderListViewModel @Inject constructor(application: Application, private val baseApi: BaseApi)
    : AndroidViewModel(application) {

    companion object {
        const val TAG = "CustomerHistoryOrderViewModel"
    }

    fun getListData(token: String, date: String) =
        Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { CustomerHistoryOderPagingSource(baseApi, token, date) })
            .liveData.map { pagingData -> pagingData.map { UiModel.HistoryOrderItem(it) } }
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
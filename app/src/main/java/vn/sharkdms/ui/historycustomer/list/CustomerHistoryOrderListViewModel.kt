package vn.sharkdms.ui.historycustomer.list

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.sharkdms.api.BaseApi
import vn.sharkdms.ui.history.list.HistoryOrder
import vn.sharkdms.ui.history.list.HistoryOrderAdapter
import vn.sharkdms.ui.history.list.HistoryOrderPagingSourceCustomer
import vn.sharkdms.ui.history.list.HistoryOrderUiModel
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
            pagingSourceFactory = { HistoryOrderPagingSourceCustomer(baseApi, token, date) })
            .liveData.map { pagingData -> pagingData.map { HistoryOrderUiModel.HistoryOrderItem(it) } }
            .map { pagingData ->
                pagingData.insertSeparators { before, after ->
                    if (after == null) return@insertSeparators null
                    val context = getApplication<Application>().applicationContext
                    if (before == null) return@insertSeparators HistoryOrderUiModel.HeaderItem(
                        Formatter.formatDate(context, after.historyOrder.orderDate)
                    )
                    if (before.historyOrder.orderDate == after.historyOrder.orderDate) null
                    else HistoryOrderUiModel.HeaderItem(Formatter.formatDate(context, after.historyOrder.orderDate))
                }
            }.cachedIn(viewModelScope)

}
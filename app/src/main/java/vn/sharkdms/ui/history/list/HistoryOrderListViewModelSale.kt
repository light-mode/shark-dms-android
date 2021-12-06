package vn.sharkdms.ui.history.list

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.sharkdms.util.Formatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryOrderListViewModelSale @Inject constructor(application: Application, private val repository: HistoryOrderListRepositorySale)
    : AndroidViewModel(application) {

    fun getListData(token: String, customerName: String, date: String) =
        repository.getListData(token, customerName, date).map { pagingData -> pagingData.map { HistoryOrderUiModel.HistoryOrderItem(it) } }
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
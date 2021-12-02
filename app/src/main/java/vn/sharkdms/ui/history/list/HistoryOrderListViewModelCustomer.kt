package vn.sharkdms.ui.history.list

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.sharkdms.util.Formatter
import javax.inject.Inject

@HiltViewModel
class HistoryOrderListViewModelCustomer @Inject constructor(application: Application,
private val repository: HistoryOrderListRepositoryCustomer)
    : AndroidViewModel(application), HistoryOrderAdapter.OnItemClickListener {

    companion object {
        const val TAG = "CustomerHistoryOrderViewModel"
    }

    fun getListData(token: String, date: String) =
        repository.getListData(token, date).map { pagingData -> pagingData.map { HistoryOrderUiModel.HistoryOrderItem(it) } }
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

    sealed class HistoryOrderListEvent {
        data class OnResponse(val code: Int, val message: String, val data: List<HistoryOrder>, val totalPage: Int): HistoryOrderListEvent()
    }

    override fun onItemClick(historyOrder: HistoryOrder) {
    }
}
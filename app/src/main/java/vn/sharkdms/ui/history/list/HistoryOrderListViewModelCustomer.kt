package vn.sharkdms.ui.history.list

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import vn.sharkdms.api.BaseApi
import vn.sharkdms.di.AppModule
import vn.sharkdms.ui.notifications.UiModel
import vn.sharkdms.util.Formatter
import javax.inject.Inject

@HiltViewModel
class HistoryOrderListViewModelCustomer @Inject constructor(application: Application, private val baseApi: BaseApi,
private val repository: HistoryOrderListRepositoryCustomer)
    : AndroidViewModel(application), HistoryOrderAdapter.OnItemClickListener {

    companion object {
        const val TAG = "CustomerHistoryOrderViewModel"
    }

    var retroService: BaseApi
    var historyOrderAdapter: HistoryOrderAdapter
    val historyOrderList: MutableLiveData<ArrayList<HistoryOrder>> by lazy {
        MutableLiveData<ArrayList<HistoryOrder>>()
    }

    init {
        historyOrderAdapter = HistoryOrderAdapter(this)
        retroService = AppModule.provideRetrofit().create(BaseApi::class.java)
    }

    private val historyOrderListEventChannel = Channel<HistoryOrderListEvent>()
    val historyOrderListEvent = historyOrderListEventChannel.receiveAsFlow()

    fun getListData(token: String, date: String) =
        repository.getListData(token, date).map { pagingData -> pagingData.map { UiModel.HistoryOrderItem(it) } }
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

    fun setAdapterData(data: ArrayList<HistoryOrder>) {
        historyOrderAdapter.setDataList(data)
    }

    sealed class HistoryOrderListEvent {
        data class OnResponse(val code: Int, val message: String, val data: List<HistoryOrder>, val totalPage: Int): HistoryOrderListEvent()
    }

    override fun onItemClick(historyOrder: HistoryOrder) {
    }
}
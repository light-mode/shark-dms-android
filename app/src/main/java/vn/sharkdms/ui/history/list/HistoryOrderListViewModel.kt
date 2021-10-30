package vn.sharkdms.ui.history.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import vn.sharkdms.api.BaseApi
import vn.sharkdms.di.AppModule
import javax.inject.Inject

@HiltViewModel
class HistoryOrderListViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {
    companion object {
        const val TAG = "HistoryOrderViewModel"
    }

    var retroService: BaseApi
    var historyOrderAdapter: HistoryOrderAdapter
    val historyOrderList: MutableLiveData<ArrayList<HistoryOrder>> by lazy {
        MutableLiveData<ArrayList<HistoryOrder>>()
    }

    init {
        historyOrderAdapter = HistoryOrderAdapter()
        retroService = AppModule.provideRetrofit().create(BaseApi::class.java)
    }

    private val historyOrderListEventChannel = Channel<HistoryOrderListEvent>()
    val historyOrderListEvent = historyOrderListEventChannel.receiveAsFlow()

    fun getListData(token: String, customerName: String, date: String): Flow<PagingData<HistoryOrder>> {
        return Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { HistoryOrderPagingSource(baseApi, token, customerName, date) }).flow.cachedIn(viewModelScope)
    }

    fun setAdapterData(data: ArrayList<HistoryOrder>) {
        historyOrderAdapter.setDataList(data)
    }

    sealed class HistoryOrderListEvent {
        data class OnResponse(val code: Int, val message: String, val data: List<HistoryOrder>, val totalPage: Int): HistoryOrderListEvent()
    }
}
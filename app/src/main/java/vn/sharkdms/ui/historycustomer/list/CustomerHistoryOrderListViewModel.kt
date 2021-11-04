package vn.sharkdms.ui.historycustomer.list

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
import vn.sharkdms.ui.base.history.list.HistoryOrder
import vn.sharkdms.ui.base.history.list.HistoryOrderAdapter
import javax.inject.Inject

@HiltViewModel
class CustomerHistoryOrderListViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel(), HistoryOrderAdapter.OnItemClickListener {

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

    fun getListData(token: String, date: String): Flow<PagingData<HistoryOrder>> {
        return Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { CustomerHistoryOderPagingSource(baseApi, token, date) }).flow.cachedIn(viewModelScope)
    }

    fun setAdapterData(data: ArrayList<HistoryOrder>) {
        historyOrderAdapter.setDataList(data)
    }

    sealed class HistoryOrderListEvent {
        data class OnResponse(val code: Int, val message: String, val data: List<HistoryOrder>, val totalPage: Int): HistoryOrderListEvent()
    }

    override fun onItemClick(historyOrder: HistoryOrder) {
    }
}
package vn.sharkdms.ui.customer.list

import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import vn.sharkdms.api.BaseApi
import vn.sharkdms.di.AppModule
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {
    companion object {
        const val TAG = "CustomerListViewModel"
    }

    var retroService: BaseApi
    var customerAdapter: CustomerAdapter
    private var token = ""
    private val customerList = MutableLiveData("")

    init {
        customerAdapter = CustomerAdapter()
        retroService = AppModule.provideRetrofit().create(BaseApi::class.java)
    }

    private val customerListEventChannel = Channel<CustomerListEvent>()
    val customerListEvent = customerListEventChannel.receiveAsFlow()

    val customers = customerList.switchMap { customerName ->
        getListData(token, customerName).cachedIn(viewModelScope)
    }

    fun getListData(token: String, customerName: String) =
        Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { CustomerPagingSource(baseApi, token, customerName) }).liveData

    fun searchCustomer(token: String, customerName: String) {
        this.token = token
        this.customerList.value = customerName
    }

    sealed class CustomerListEvent {
        data class OnResponse(val code: Int, val message: String, val data: List<Customer>, val totalPage: Int) : CustomerListEvent()
    }
}
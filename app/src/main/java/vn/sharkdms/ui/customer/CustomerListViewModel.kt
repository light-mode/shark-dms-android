package vn.sharkdms.ui.customer

import android.util.Log
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
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.CustomerListRequest
import vn.sharkdms.di.AppModule
import vn.sharkdms.ui.forgotpassword.ForgotPasswordViewModel
import java.lang.NumberFormatException
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {
    companion object {
        const val TAG = "CustomerListViewModel"
    }

    lateinit var  retroService: BaseApi
    lateinit var customerAdapter: CustomerAdapter
    val customerList: MutableLiveData<ArrayList<Customer>> by lazy {
        MutableLiveData<ArrayList<Customer>>()
    }

    init {
        customerAdapter = CustomerAdapter()
        retroService = AppModule.provideRetrofit().create(BaseApi::class.java)
    }

    private val customerListEventChannel = Channel<CustomerListEvent>()
    val customerListEvent = customerListEventChannel.receiveAsFlow()

    fun getListData(customerName: String): Flow<PagingData<Customer>> {
        return Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {CustomerPagingSource(baseApi, customerName)}).flow.cachedIn(viewModelScope)
    }

    fun setAdapterData(data: ArrayList<Customer>) {
        customerAdapter.setDataList(data)
    }

    sealed class CustomerListEvent {
        data class OnResponse(val code: Int, val message: String, val data: List<Customer>, val totalPage: Int) : CustomerListEvent()
    }
}
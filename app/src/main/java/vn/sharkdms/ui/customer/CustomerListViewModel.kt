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
import vn.sharkdms.ui.forgotpassword.ForgotPasswordViewModel
import java.lang.NumberFormatException
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {
    companion object {
        const val TAG = "CustomerListViewModel"
    }

    lateinit var customerList: MutableLiveData<CustomerList>
    lateinit var customerAdapter: CustomerAdapter

    init {
        customerList = MutableLiveData()
        customerAdapter = CustomerAdapter()
    }

    private val customerListEventChannel = Channel<CustomerListEvent>()
    val customerListEvent = customerListEventChannel.receiveAsFlow()

    fun customerListRequest(page: Int, customerName: String) {
        viewModelScope.launch {
            val body = CustomerListRequest(page, customerName)
            val response = baseApi.listCustomer(body)
            try {
                val code = response.code.toInt()
                customerListEventChannel.send(
                    CustomerListEvent.OnResponse(code, response.message, response.data, response.totalPage)
                )
            }catch (nfe: NumberFormatException) {
                Log.e(TAG, nfe.message, nfe)
            }
        }
    }

    fun getCustomerListDataObserver(): MutableLiveData<CustomerList> {
        return customerList
    }

    fun getAdapter(): CustomerAdapter {
        return customerAdapter
    }

//    fun setAdapterData(data: List<Customer>){
//        customerAdapter.setDataList(data)
//        customerAdapter.notifyDataSetChanged()
//    }

    fun getListData(customerName: String): Flow<PagingData<Customer>> {
        return Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {CustomerPagingSource(baseApi, customerName)}).flow.cachedIn(viewModelScope)
    }

    sealed class CustomerListEvent {
        data class OnResponse(val code: Int, val message: String, val data: CustomerList, val totalPage: Int) : CustomerListEvent()
    }
}
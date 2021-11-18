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
class CustomerListViewModel @Inject constructor(private val repository: CustomerListRepository) : ViewModel() {
    companion object {
        const val TAG = "CustomerListViewModel"
    }

    private var token = ""
    private val customerList = MutableLiveData("")

    val customers = customerList.switchMap { customerName ->
        getListData(token, customerName).cachedIn(viewModelScope)
    }

    fun getListData(token: String, customerName: String) = repository.getListData(token, customerName)

    fun searchCustomer(token: String, customerName: String) {
        this.token = token
        this.customerList.value = customerName
    }

}
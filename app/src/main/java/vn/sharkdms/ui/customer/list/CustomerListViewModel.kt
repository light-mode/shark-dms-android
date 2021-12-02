package vn.sharkdms.ui.customer.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private fun getListData(token: String, customerName: String) = repository.getListData(token, customerName)

    fun searchCustomer(token: String, customerName: String) {
        this.token = token
        this.customerList.value = customerName
    }

}
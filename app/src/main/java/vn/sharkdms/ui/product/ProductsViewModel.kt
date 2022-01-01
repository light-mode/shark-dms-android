package vn.sharkdms.ui.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.sharkdms.api.Customer
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository) : ViewModel() {
    companion object {
        private const val DEFAULT_SEARCH_NAME = ""
    }

    private var token = ""
    private val currentName = MutableLiveData(DEFAULT_SEARCH_NAME)
    private var customer: Customer? = null

    val tasks = currentName.switchMap { name ->
        repository.getSearchResults(token, name, customer).cachedIn(viewModelScope)
    }

    fun searchProducts(token: String, name: String, customer: Customer?) {
        this.token = token
        currentName.value = name
        this.customer = customer
    }
}
package vn.sharkdms.ui.products

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository) : ViewModel() {
    companion object {
        private const val DEFAULT_SEARCH_NAME = ""
    }

    private var token = ""
    private val currentName = MutableLiveData(DEFAULT_SEARCH_NAME)

    val tasks = currentName.switchMap { name ->
        repository.getSearchResults(token, name).cachedIn(viewModelScope)
    }

    fun searchProducts(token: String, name: String) {
        this.token = token
        currentName.value = name
    }
}
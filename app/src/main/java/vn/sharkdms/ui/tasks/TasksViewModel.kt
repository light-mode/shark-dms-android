package vn.sharkdms.ui.tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {
    companion object {
        private const val DEFAULT_SEARCH_DATE = ""
    }

    private var token = ""
    val currentSearchDate = MutableLiveData(DEFAULT_SEARCH_DATE)

    val tasks = currentSearchDate.switchMap { searchDate ->
        repository.getSearchResults(token, searchDate).cachedIn(viewModelScope)
    }

    fun searchTasks(token: String, searchDate: String) {
        this.token = token
        currentSearchDate.value = searchDate
    }
}
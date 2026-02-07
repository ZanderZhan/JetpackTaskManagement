package com.example.jetpacktaskmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.jetpacktaskmanagement.ThemeDataStore
import com.example.jetpacktaskmanagement.dao.TaskDao
import com.example.jetpacktaskmanagement.entity.Task
import com.example.jetpacktaskmanagement.entity.UserWithTasks
import com.example.jetpacktaskmanagement.model.IUiState
import com.example.jetpacktaskmanagement.model.UIState
import com.example.jetpacktaskmanagement.model.UiStateViewModel
import com.example.jetpacktaskmanagement.repository.TaskPagingSource
import com.example.jetpacktaskmanagement.repository.TaskRepository
import com.example.jetpacktaskmanagement.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
    private val userRepository: UserRepository,
    private val themeDataStore: ThemeDataStore,
    uiStateViewModel: IUiState = UiStateViewModel(UIState.Loading)
) : IUiState by uiStateViewModel, UserViewModel(userRepository) {

    private val _queryString = MutableLiveData(savedStateHandle["query"] ?: "")
    val queryString: LiveData<String> = _queryString

    private var _userWithTasks: LiveData<UserWithTasks?> = currentUser.switchMap { user ->
        if (user != null) {
            viewModelScope.launch {
                val result = repository.refreshUserTasks(user.id)
                result.onFailure { throwable ->
                    Log.e(TAG, "Failed to refresh user tasks", throwable)
                }
            }
            userRepository.getSpecificUserWithTasks(user.id)
        } else {
            MutableLiveData(null)
        }

    }

    val userWithTasks = MediatorLiveData<UserWithTasks?>().apply {
        fun updateTasks() {
            _userWithTasks.value?.let {

                var tasks = _userWithTasks.value?.tasks.orEmpty()
                val query = _queryString.value.orEmpty()

                var result = tasks.filter { it.description.contains(query, ignoreCase = true) }

                result = result.sortedBy { it.checked }

                value = UserWithTasks(it.user, result)
            }

        }

        addSource(_userWithTasks) {
            updateTasks()
        }
        addSource(_queryString) {
            updateTasks()
        }
    }


    val taskPagingData: Flow<PagingData<Task>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            TaskPagingSource(repository)
        }
    )
        .flow
        .cachedIn(viewModelScope)

    init {
        uiStateViewModel.addSource(userWithTasks) { userWithTasks ->
            when (userWithTasks) {
                null -> UIState.Loading
                else -> UIState.Success
            }
        }
    }

    fun addTask(description: String) {
        viewModelScope.launch {
            val userId = currentUser.value?.id ?: return@launch

            val newTask =
                Task(0, userId, false, description, System.currentTimeMillis())
            taskDao.saveTasks(listOf(newTask))
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(checked = !task.checked)
            taskDao.saveTasks(listOf(updatedTask))
        }
    }

    fun search(query: String) {
        savedStateHandle["query"] = query
        _queryString.value = query
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = themeDataStore.isDarkTheme.first()
            themeDataStore.setTheme(!currentTheme)
        }
    }

    companion object {
        private const val TAG = "TaskListViewModel"
    }
}

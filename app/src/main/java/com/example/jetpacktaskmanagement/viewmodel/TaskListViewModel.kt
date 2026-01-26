package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jetpacktaskmanagement.TaskApplication
import com.example.jetpacktaskmanagement.dao.TaskDao
import com.example.jetpacktaskmanagement.entity.Task
import com.example.jetpacktaskmanagement.model.UIState
import com.example.jetpacktaskmanagement.repository.TaskListRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class TaskListViewModel(
    private val taskDao: TaskDao,
    private val savedStateHandle: SavedStateHandle,
    private val repository: TaskListRepository,
) : ViewModel() {

    private val _localTasks = taskDao.getAll()

    private val _networkTasks = MutableLiveData<List<Task>>(repository.getNetworkTasks())

    private val _queryString = MutableLiveData(savedStateHandle["query"] ?: "")
    val queryString: LiveData<String> = _queryString

    private val _tasks = MediatorLiveData<List<Task>>()

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState

    // switchMap will trigger a problem:
    // _tasks may not be attached to UI at first, so it may not initialise through
    // addSource onChange.
    // this will lead to: if _queryString is not empty at start, the screen may
    // produce an Error uiState.
    val tasks: LiveData<List<Task>> = _queryString.switchMap { query ->
        val baseTasks = if (query.isEmpty()) _tasks else MutableLiveData(_search(query))
        // Sorting happens here in the ViewModel
        baseTasks.map { list -> list.sortedBy { it.checked } }
    }

    private val _showSnacked = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val showSnacked: SharedFlow<Boolean> = _showSnacked


    init {
        _tasks.addSource(_localTasks) { local ->
            _uiState.value = UIState.Success
            _tasks.value = local.orEmpty() + _networkTasks.value.orEmpty()
            _search(_queryString.value.orEmpty())
        }
        _tasks.addSource(_networkTasks) { network ->
            _uiState.value = UIState.Success
            _tasks.value = _localTasks.value.orEmpty() + network.orEmpty()
            _search(_queryString.value.orEmpty())
        }
    }

    private fun _search(query: String): List<Task> {
        val currentTasks = _tasks.value.orEmpty().filter {
            it.description.contains(query, ignoreCase = true)
        }
        if (currentTasks.isEmpty()) {
            _uiState.value = UIState.Error
            _showSnacked.tryEmit(true)
        } else {
            _uiState.value = UIState.Success
        }
        return currentTasks
    }

    fun addTask(description: String) {
        viewModelScope.launch {
            val newTask = Task(0, false, description, System.currentTimeMillis())
            taskDao.saveTasks(listOf(newTask))
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    fun toggleTask(task: Task) {
        val updatedTask = task.copy(checked = !task.checked)
        val networkTasks = _networkTasks.value.orEmpty()
        if (networkTasks.any { it == task }) {
            _networkTasks.value = networkTasks.map { if (it == task) updatedTask else it }
            return
        }
        viewModelScope.launch {
            taskDao.saveTasks(listOf(updatedTask))
        }
    }

    fun search(query: String) {
        savedStateHandle["query"] = query
        _queryString.value = query
    }

    fun clearSearch(query: String) {
        savedStateHandle["query"] = ""
        _queryString.value = ""
    }

    companion object {
        val REPOSITORY_KEY = object : CreationExtras.Key<TaskListRepository> {}

        val taskListRepository = TaskListRepository()

        fun provideFactory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
//                    val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    this.get(REPOSITORY_KEY)
                    val repository = this[REPOSITORY_KEY]
                        ?: throw IllegalArgumentException("Repository not provided in extras")
                    val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TaskApplication
                    val savedStateHandle = createSavedStateHandle()
                    TaskListViewModel(application.database.taskDao(), savedStateHandle, repository)
                }
            }
        }
    }
}

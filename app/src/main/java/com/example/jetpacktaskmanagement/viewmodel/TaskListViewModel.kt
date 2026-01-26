package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jetpacktaskmanagement.TaskApplication
import com.example.jetpacktaskmanagement.dao.TaskDao
import com.example.jetpacktaskmanagement.dao.UserDao
import com.example.jetpacktaskmanagement.entity.Task
import com.example.jetpacktaskmanagement.entity.UserWithTasks
import com.example.jetpacktaskmanagement.model.UIState
import com.example.jetpacktaskmanagement.repository.TaskListRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


class TaskListViewModel(
    private val taskDao: TaskDao,
    private val userDao: UserDao,
    private val savedStateHandle: SavedStateHandle,
    private val repository: TaskListRepository,
) : UserViewModel(userDao) {

    // todo 1: implement task query
    private val _queryString = MutableLiveData(savedStateHandle["query"] ?: "")
    val queryString: LiveData<String> = _queryString

    // todo 2: move uiState to another place, making it reusable
    private val _uiState = MediatorLiveData(UIState.Loading)
    val uiState: LiveData<UIState> = _uiState

    // todo 3: implement a MutableStateFlow data

    //    private val _tasks = MediatorLiveData<List<Task>>()

    // switchMap will trigger a problem:
    // _tasks may not be attached to UI at first, so it may not initialise through
    // addSource onChange.
    // this will lead to: if _queryString is not empty at start, the screen may
    // produce an Error uiState.
//    val tasks: LiveData<List<Task>> = _queryString.switchMap { query ->
//        val baseTasks = if (query.isEmpty()) _tasks else MutableLiveData(_search(query))
//        // Sorting happens here in the ViewModel
//        baseTasks.map { list -> list.sortedBy { it.checked } }
//    }

    val userWithTasks: LiveData<UserWithTasks?> = currentUser.switchMap { user ->
        if (user != null) {
            userDao.getSpecificUserWithTasks(user.id)
        } else {
            MutableLiveData(null)
        }
    }

    private val _showSnacked = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val showSnacked: SharedFlow<Boolean> = _showSnacked


    init {
//        _tasks.addSource(_localTasks) { local ->
//            _uiState.value = UIState.Success
//            _tasks.value = local.orEmpty() + _networkTasks.value.orEmpty()
//            _search(_queryString.value.orEmpty())
//        }
//        _tasks.addSource(_networkTasks) { network ->
//            _uiState.value = UIState.Success
//            _tasks.value = _localTasks.value.orEmpty() + network.orEmpty()
//            _search(_queryString.value.orEmpty())
//        }

        viewModelScope.launch {
            var tasks = repository.getNetworkTasks()
            taskDao.saveTasks(tasks)
        }

        _uiState.addSource(userWithTasks) { userWithTasks ->
            if (userWithTasks == null) {
                _uiState.value = UIState.Loading
            } else if (userWithTasks.tasks.isEmpty()) {
                _uiState.value = UIState.Error
            } else {
                _uiState.value = UIState.Success
            }
        }
    }

    private fun _search(query: String): List<Task> {
        val currentTasks = userWithTasks.value?.tasks.orEmpty().filter {
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
            val newTask =
                Task(0, currentUser.value?.id ?: 0, false, description, System.currentTimeMillis())
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
            taskDao.saveTasks(listOf(task))
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
                    val application =
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TaskApplication
                    val savedStateHandle = createSavedStateHandle()
                    TaskListViewModel(
                        application.database.taskDao(),
                        application.database.userDao(),
                        savedStateHandle,
                        repository
                    )
                }
            }
        }
    }
}

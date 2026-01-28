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
import com.example.jetpacktaskmanagement.model.IUiState
import com.example.jetpacktaskmanagement.model.UIState
import com.example.jetpacktaskmanagement.model.UiStateViewModel
import com.example.jetpacktaskmanagement.repository.TaskListRepository
import kotlinx.coroutines.launch


class TaskListViewModel(
    private val taskDao: TaskDao,
    private val userDao: UserDao,
    private val savedStateHandle: SavedStateHandle,
    private val repository: TaskListRepository,
    uiStateViewModel: IUiState = UiStateViewModel(UIState.Loading)
) : IUiState by uiStateViewModel, UserViewModel(userDao) {

    private val _queryString = MutableLiveData(savedStateHandle["query"] ?: "")
    val queryString: LiveData<String> = _queryString

    private var _userWithTasks: LiveData<UserWithTasks?> = currentUser.switchMap { user ->
        if (user != null) {
            userDao.getSpecificUserWithTasks(user.id)
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

    init {
        viewModelScope.launch {
            var tasks = repository.getNetworkTasks()
            taskDao.saveTasks(tasks)
        }

        uiStateViewModel.addSource(userWithTasks) { userWithTasks ->
            when (userWithTasks) {
                null -> UIState.Loading
                else -> UIState.Success
            }
        }
    }

    fun addTask(description: String) {
        viewModelScope.launch {
            val userId = currentUser.value?.id
            if (userId == null) {
                // No current user selected; do not create an orphaned task
                return@launch
            }

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

    companion object {
        val REPOSITORY_KEY = object : CreationExtras.Key<TaskListRepository> {}

        val taskListRepository = TaskListRepository()

        fun provideFactory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
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

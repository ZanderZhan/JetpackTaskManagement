package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jetpacktaskmanagement.model.Task
import com.example.jetpacktaskmanagement.repository.TaskListRepository
import java.util.Date

class TaskListViewModel(private val repository: TaskListRepository) : ViewModel() {

    private val _localTasks = MutableLiveData<List<Task>>(
        repository.getLocalTasks()
    )

    private val _networkTasks = MutableLiveData<List<Task>>(
        repository.getNetworkTasks()
    )

    private val _queryString = MutableLiveData("")

    private val _tasks = MediatorLiveData<List<Task>>()

    val tasks: LiveData<List<Task>> = _queryString.switchMap { query ->
        if (query.isEmpty()) return@switchMap _tasks
        val currentTasks = _tasks.value.orEmpty().filter {
            it.description.contains(query, ignoreCase = true)
        }
        return@switchMap MutableLiveData(currentTasks)
    }


    init {
        _tasks.addSource(_localTasks) { local ->
            _tasks.value = local.orEmpty() + _networkTasks.value.orEmpty()
        }
        _tasks.addSource(_networkTasks) { network ->
            _tasks.value = _localTasks.value.orEmpty() + network.orEmpty()
        }
    }

    fun addTask(description: String) {
        val currentTasks = _tasks.value.orEmpty().toMutableList()
        currentTasks.add(Task(false, description, Date()))
        _tasks.value = currentTasks
    }

    fun removeTask(task: Task) {
        val currentTasks = _tasks.value.orEmpty().toMutableList()
        currentTasks.remove(task)
        _tasks.value = currentTasks
    }

    fun toggleTask(task: Task) {
        val currentTasks = _tasks.value.orEmpty().map {
            if (it == task) it.copy(checked = !it.checked) else it
        }
        _tasks.value = currentTasks
    }

    fun search(query: String) {
        _queryString.value = query
    }

    fun clearSearch(query: String) {
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
                    val repository = this[REPOSITORY_KEY] ?: throw IllegalArgumentException("Repository not provided in extras")
                    TaskListViewModel(repository)
                }
            }
        }
    }
}

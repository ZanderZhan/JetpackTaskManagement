package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jetpacktaskmanagement.model.Task
import java.util.Date

class TaskListViewModel : ViewModel() {
    private fun getRandomDate(): Date {
        val tenDaysInMs = 10L * 24 * 60 * 60 * 1000
        val randomOffset = (0..tenDaysInMs).random()
        return Date(System.currentTimeMillis() - randomOffset)
    }

    private val _localTasks = MutableLiveData<List<Task>>(
        listOf(
            Task(false, "Buy groceries", getRandomDate()),
            Task(true, "Finish project proposal", getRandomDate()),
            Task(false, "Call mom", getRandomDate()),
            Task(false, "Go for a run", getRandomDate()),
            Task(true, "Read a book", getRandomDate()),
            Task(false, "Water the plants", getRandomDate()),
            Task(false, "Clean the kitchen", getRandomDate()),
            Task(true, "Pay bills", getRandomDate()),
            Task(false, "Schedule dentist appointment", getRandomDate()),
            Task(false, "Organize workspace", getRandomDate())
        )
    )

    private val _networkTasks = MutableLiveData<List<Task>>(
        listOf(
            Task(false, "Buy groceries", getRandomDate()),
            Task(true, "Finish project proposal", getRandomDate()),
        )
    )

    private val _tasks = MediatorLiveData<List<Task>>()


    val tasks: LiveData<List<Task>> = _tasks

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
}

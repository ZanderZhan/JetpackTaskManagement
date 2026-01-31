package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.jetpacktaskmanagement.TaskApplication
import com.example.jetpacktaskmanagement.entity.TaskWithTags
import com.example.jetpacktaskmanagement.repository.RetrofitClient
import com.example.jetpacktaskmanagement.repository.TagRepository
import com.example.jetpacktaskmanagement.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val repository: TaskRepository,
    private val tagRepository: TagRepository,
    private val taskId: Int,
) : ViewModel() {

    private val _task = repository.getTaskWithTags(taskId)

    val task: LiveData<TaskWithTags> = _task

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = tagRepository.refreshTags(taskId)
            result.onFailure { throwable ->
                android.util.Log.e("TaskDetailViewModel", "Failed to refresh tags", throwable)
            }
        }
    }

    companion object {

        fun provideFactory(taskId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    if (!modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                    }
                    val application =
                        checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? TaskApplication)
                    val repository =
                        TaskRepository(RetrofitClient.taskService, application.database.taskDao())
                    val tagRepository =
                        TagRepository(RetrofitClient.tagService, application.database.tagDao())
                    @Suppress("UNCHECKED_CAST")
                    return TaskDetailViewModel(repository, tagRepository, taskId) as T
                }
            }

    }


}
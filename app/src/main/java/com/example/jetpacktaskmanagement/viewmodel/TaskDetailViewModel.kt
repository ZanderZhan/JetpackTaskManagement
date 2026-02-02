package com.example.jetpacktaskmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktaskmanagement.entity.TaskWithTags
import com.example.jetpacktaskmanagement.repository.TagRepository
import com.example.jetpacktaskmanagement.repository.TaskRepository
import com.example.jetpacktaskmanagement.screen.TaskDetail
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TaskDetailViewModel.Factory::class)
class TaskDetailViewModel @AssistedInject constructor(
    private val repository: TaskRepository,
    private val tagRepository: TagRepository,
    @Assisted private val taskDetail: TaskDetail,
) : ViewModel() {

    companion object {
        private const val TAG = "TaskDetailViewModel"
    }

    private val taskId: Int = taskDetail.taskId

    private val _task = repository.getTaskWithTags(taskId)

    val task: LiveData<TaskWithTags> = _task

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = tagRepository.refreshTags(taskId)
            result.onFailure { throwable ->
                Log.e(TAG, "Failed to refresh tags", throwable)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(taskDetail: TaskDetail): TaskDetailViewModel
    }
}
package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.jetpacktaskmanagement.TaskApplication
import com.example.jetpacktaskmanagement.entity.Tag
import com.example.jetpacktaskmanagement.entity.Task
import com.example.jetpacktaskmanagement.repository.RetrofitClient
import com.example.jetpacktaskmanagement.repository.TagRepository
import com.example.jetpacktaskmanagement.repository.TaskRepository
import kotlinx.coroutines.launch

class TagViewModel(
    private val tagId: Int,
    private val repository: TagRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    val tagWithTasks: LiveData<Map<Tag, List<Task>>> = repository.getTagWithTasks(tagId)

    init {
        viewModelScope.launch {
            taskRepository.refreshTasksByTagId(tagId)
        }
    }

    companion object {
        fun provideFactory(
            tagId: Int
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    if (!modelClass.isAssignableFrom(TagViewModel::class.java)) {
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                    }
                    val application: TaskApplication =
                        checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? TaskApplication)
                    val repository =
                        TagRepository(RetrofitClient.tagService, application.database.tagDao())
                    val taskRepository =
                        TaskRepository(
                            RetrofitClient.taskService,
                            application.database.taskDao(),
                            application.database.tagDao()
                        )
                    @Suppress("UNCHECKED_CAST")
                    return TagViewModel(tagId, repository, taskRepository) as T
                }
            }
        }
    }
}
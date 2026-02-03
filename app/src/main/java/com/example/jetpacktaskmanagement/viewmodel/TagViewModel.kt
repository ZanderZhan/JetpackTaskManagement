package com.example.jetpacktaskmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktaskmanagement.entity.Tag
import com.example.jetpacktaskmanagement.entity.Task
import com.example.jetpacktaskmanagement.repository.TagRepository
import com.example.jetpacktaskmanagement.repository.TaskRepository
import com.example.jetpacktaskmanagement.screen.TagKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TagViewModel.Factory::class)
class TagViewModel @AssistedInject constructor(
    @Assisted private val tagKey: TagKey,
    private val repository: TagRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "TagViewModel"
    }

    private val tagId: Int = tagKey.tagId

    val tagWithTasks: LiveData<Map<Tag, List<Task>>> = repository.getTagWithTasks(tagId)

    init {
        viewModelScope.launch {
            val result = taskRepository.refreshTasksByTagId(tagId)
            result.onFailure { throwable ->
                Log.e(TAG, "Failed to refresh tasks by tag", throwable)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(tagKey: TagKey): TagViewModel
    }
}
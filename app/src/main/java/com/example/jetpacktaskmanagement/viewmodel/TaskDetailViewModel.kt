package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.jetpacktaskmanagement.TaskApplication
import com.example.jetpacktaskmanagement.dao.TaskDao
import com.example.jetpacktaskmanagement.entity.TaskWithTags

class TaskDetailViewModel(
    private val taskDao: TaskDao,
    private val taskId: Int,
) : ViewModel() {

    private val _task = taskDao.getTaskWithTags(taskId)

    val task: LiveData<TaskWithTags> = _task


    companion object {

        fun provideFactory(taskId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val application =
                        checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? TaskApplication)
                    return TaskDetailViewModel(application.database.taskDao(), taskId) as T
                }
            }

    }


}
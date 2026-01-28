package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.jetpacktaskmanagement.TaskApplication
import com.example.jetpacktaskmanagement.dao.TagDao

class TagViewModel(
    private val tagId: Int,
    private val tagDao: TagDao,
) : ViewModel() {

    val tagWithTasks = tagDao.getTagWithTasks(tagId)

    companion object {
        fun provideFactory(
            tagId: Int
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val application: TaskApplication =
                        checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TaskApplication)
                    @Suppress("UNCHECKED_CAST")
                    return TagViewModel(tagId, application.database.tagDao()) as T
                }
            }
        }
    }
}
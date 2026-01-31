package com.example.jetpacktaskmanagement.repository

import android.util.Log
import com.example.jetpacktaskmanagement.dao.TaskDao
import com.example.jetpacktaskmanagement.service.TaskService


class TaskRepository(
    private val service: TaskService,
    private val taskDao: TaskDao,
) {

    companion object {
        private const val TAG = "TaskRepository"
    }

    fun getTaskWithTags(taskId: Int) = taskDao.getTaskWithTags(taskId)

    suspend fun refreshUserTasks(userId: Int): Result<Unit> {
        return try {
            var tasks = service.getUserTasks(userId)
            tasks = tasks.map { it.copy(userId = userId) }
            taskDao.saveTasks(tasks)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing user tasks", e)
            Result.failure(e)
        }
    }

}

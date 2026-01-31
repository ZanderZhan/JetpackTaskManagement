package com.example.jetpacktaskmanagement.repository

import android.util.Log
import com.example.jetpacktaskmanagement.dao.TagDao
import com.example.jetpacktaskmanagement.entity.TaskWithTagCrossRef
import com.example.jetpacktaskmanagement.service.TagService

class TagRepository(private val tagService: TagService, private val tagDao: TagDao) {

    companion object {
        private const val TAG = "TagRepository"
    }

    fun getTagWithTasks(tagId: Int) = tagDao.getTagWithTasks(tagId)

    suspend fun refreshTags(taskId: Int): Result<Unit> {
        return try {
            val tags = tagService.getTagByTaskId(taskId)
            tagDao.saveTags(tags)
            val crossRefs = tags.map { TaskWithTagCrossRef(taskId, it.id) }
            tagDao.saveTaskWithTagCrossRefs(crossRefs)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing tags", e)
            Result.failure(e)
        }
    }

    suspend fun refreshAllTags(): Result<Unit> {
        return try {
            val tags = tagService.getAllTags()
            tagDao.saveTags(tags)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing all tags", e)
            Result.failure(e)
        }
    }

}
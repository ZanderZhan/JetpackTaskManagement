package com.example.jetpacktaskmanagement.repository

import com.example.jetpacktaskmanagement.dao.TagDao
import com.example.jetpacktaskmanagement.entity.TaskWithTagCrossRef
import com.example.jetpacktaskmanagement.service.TagService

class TagRepository(private val tagService: TagService, private val tagDao: TagDao) {

    fun getTagWithTasks(tagId: Int) = tagDao.getTagWithTasks(tagId)

    suspend fun refreshTags(taskId: Int): Result<Unit> {
        return try {
            val tags = tagService.getTagByTaskId(taskId)
            tagDao.saveTags(tags)
            val crossRefs = tags.map { TaskWithTagCrossRef(taskId, it.id) }
            tagDao.saveTaskWithTagCrossRefs(crossRefs)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

}
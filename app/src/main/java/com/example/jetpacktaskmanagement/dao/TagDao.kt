package com.example.jetpacktaskmanagement.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.jetpacktaskmanagement.entity.Tag
import com.example.jetpacktaskmanagement.entity.Task

@Dao
interface TagDao {

    @Query(
        "SELECT * FROM tags " +
                "JOIN task_tag_cross_ref ON tags.id = task_tag_cross_ref.tagId " +
                "JOIN tasks ON task_tag_cross_ref.taskId = tasks.id " +
                "WHERE tags.id = :tagId"
    )
    fun getTagWithTasks(tagId: Int): LiveData<Map<Tag, List<Task>>>
}
package com.example.jetpacktaskmanagement.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.jetpacktaskmanagement.entity.TagWithTasks

@Dao
interface TagDao {

    @Transaction
    @Query("SELECT * FROM tags WHERE id = :tagId")
    fun getTagWithTasks(tagId: Int): LiveData<TagWithTasks>
}
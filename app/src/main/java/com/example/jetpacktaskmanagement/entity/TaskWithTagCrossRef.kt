package com.example.jetpacktaskmanagement.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "task_tag_cross_ref",
    primaryKeys = ["taskId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = Task::class, parentColumns = ["id"], childColumns = ["taskId"]),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tagId"])
    ]
)
data class TaskWithTagCrossRef(
    val taskId: Int,
    val tagId: Int,
)
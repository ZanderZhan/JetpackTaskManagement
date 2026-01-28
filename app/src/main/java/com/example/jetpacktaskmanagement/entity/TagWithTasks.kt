package com.example.jetpacktaskmanagement.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TagWithTasks(
    @Embedded val tag: Tag,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            TaskWithTagCrossRef::class,
            parentColumn = "tagId",
            entityColumn = "taskId"
        )
    )
    val tasks: List<Task>
)

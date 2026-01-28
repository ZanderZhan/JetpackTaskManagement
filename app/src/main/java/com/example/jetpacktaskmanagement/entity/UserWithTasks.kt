package com.example.jetpacktaskmanagement.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithTasks(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId",
        entity = Task::class
    )
    val tasks: List<Task>
)
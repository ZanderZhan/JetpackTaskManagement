package com.example.jetpacktaskmanagement.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tags",
)
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val priority: TagPriority,
)

enum class TagPriority {
    LOW,
    MEDIUM,
    HIGH
}
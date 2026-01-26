package com.example.jetpacktaskmanagement.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val userId: Int,
    @ColumnInfo val checked: Boolean,
    @ColumnInfo val description: String,
    @ColumnInfo val date: Long,
)
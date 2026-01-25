package com.example.jetpacktaskmanagement.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(
    tableName = "tasks",
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val checked: Boolean,
    @ColumnInfo val description: String,
    @ColumnInfo val date: String,
)
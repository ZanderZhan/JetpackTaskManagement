package com.example.jetpacktaskmanagement.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo val name: String = "",

    @ColumnInfo val gender: Gender = Gender.UNSPECIFIED
)

enum class Gender {
    UNSPECIFIED,
    MALE,
    FEMALE,
}
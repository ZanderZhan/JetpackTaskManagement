package com.example.jetpacktaskmanagement.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpacktaskmanagement.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)
}
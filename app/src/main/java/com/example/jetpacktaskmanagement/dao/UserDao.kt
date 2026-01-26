package com.example.jetpacktaskmanagement.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.jetpacktaskmanagement.entity.User
import com.example.jetpacktaskmanagement.entity.UserWithTasks

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: List<User>)

    @Transaction
    @Query("SELECT * FROM users")
    fun getUsersWithTasks(): LiveData<List<UserWithTasks>>

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getSpecificUserWithTasks(userId: Int): LiveData<UserWithTasks?>

}
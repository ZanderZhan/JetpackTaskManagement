package com.example.jetpacktaskmanagement.repository

import androidx.lifecycle.LiveData
import com.example.jetpacktaskmanagement.dao.UserDao
import com.example.jetpacktaskmanagement.entity.User
import com.example.jetpacktaskmanagement.entity.UserWithTasks
import com.example.jetpacktaskmanagement.service.UserService

class UserRepository(private val userService: UserService, private val userDao: UserDao) {

    suspend fun refreshUsers(): Result<Unit> {
        return try {
            val networkUsers = userService.getUsers()
            userDao.insertAll(networkUsers)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun getUsersLiveData(): LiveData<List<User>> = userDao.getAllUsers()

    fun getSpecificUserWithTasks(userId: Int): LiveData<UserWithTasks?> =
        userDao.getSpecificUserWithTasks(userId)

}
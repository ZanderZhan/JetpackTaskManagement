package com.example.jetpacktaskmanagement.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.jetpacktaskmanagement.dao.UserDao
import com.example.jetpacktaskmanagement.entity.User
import com.example.jetpacktaskmanagement.entity.UserWithTasks
import com.example.jetpacktaskmanagement.service.UserService
import retrofit2.HttpException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val userDao: UserDao
) {

    companion object {
        private const val TAG = "UserRepository"
    }

    suspend fun refreshUsers(): Result<Unit> {
        return try {
            val networkUsers = userService.getUsers()
            userDao.insertAll(networkUsers)
            Result.success(Unit)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e(TAG, "HTTP ${e.code()}: $errorBody")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing users", e)
            Result.failure(e)
        }
    }

    fun getUsersLiveData(): LiveData<List<User>> = userDao.getAllUsers()

    fun getSpecificUserWithTasks(userId: Int): LiveData<UserWithTasks?> =
        userDao.getSpecificUserWithTasks(userId)

}
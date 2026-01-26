package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.jetpacktaskmanagement.dao.UserDao
import com.example.jetpacktaskmanagement.entity.User

abstract class UserViewModel(private val userDao: UserDao) : ViewModel() {

    private val _users = userDao.getAllUsers()

    private val _currentUser = MediatorLiveData<User?>()

    val currentUser: LiveData<User?> = _currentUser
    
    val allUsers: LiveData<List<User>> = _users

    init {
        _currentUser.addSource(_users) { users ->
            if (_currentUser.value == null) {
                _currentUser.value = users.firstOrNull()
            }
        }
    }

    fun switchToUser(user: User) {
        _currentUser.value = user
    }
}
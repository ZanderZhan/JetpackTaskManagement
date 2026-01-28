package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.jetpacktaskmanagement.dao.UserDao
import com.example.jetpacktaskmanagement.entity.User

open class UserViewModel(private val userDao: UserDao) : ViewModel() {

    private val _users = userDao.getAllUsers()

    private val _currentUser = MediatorLiveData<User?>()
    
    private var isUserExplicitlySelected: Boolean = false

    val currentUser: LiveData<User?> = _currentUser
    
    val allUsers: LiveData<List<User>> = _users

    init {
        _currentUser.addSource(_users) { users ->
            if (!isUserExplicitlySelected && _currentUser.value == null) {
                _currentUser.value = users.firstOrNull()
            }
        }
    }

    fun switchToUser(user: User) {
        isUserExplicitlySelected = true
        _currentUser.value = user
    }
}
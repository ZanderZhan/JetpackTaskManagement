package com.example.jetpacktaskmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktaskmanagement.entity.User
import com.example.jetpacktaskmanagement.repository.UserRepository
import kotlinx.coroutines.launch

open class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _users = userRepository.getUsersLiveData()

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

        refreshUsers()
    }

    fun refreshUsers() {
        viewModelScope.launch {
            val result = userRepository.refreshUsers()
            if (result.isFailure) {
                // Handle error (repository is responsible for logging)
            }
        }
    }

    fun switchToUser(user: User) {
        isUserExplicitlySelected = true
        _currentUser.value = user
    }
}
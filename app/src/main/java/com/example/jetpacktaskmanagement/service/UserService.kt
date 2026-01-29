package com.example.jetpacktaskmanagement.service

import com.example.jetpacktaskmanagement.entity.User
import retrofit2.http.GET

interface UserService {

    @GET("api/users")
    suspend fun getUsers(): List<User>
}
package com.example.jetpacktaskmanagement.repository

import com.example.jetpacktaskmanagement.service.TagService
import com.example.jetpacktaskmanagement.service.TaskService
import com.example.jetpacktaskmanagement.service.UserService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userService by lazy {
        retrofit.create(UserService::class.java)
    }

    val taskService by lazy {
        retrofit.create(TaskService::class.java)
    }

    val tagService by lazy {
        retrofit.create(TagService::class.java)
    }
}
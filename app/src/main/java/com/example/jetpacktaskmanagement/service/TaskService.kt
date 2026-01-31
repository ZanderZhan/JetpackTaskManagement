package com.example.jetpacktaskmanagement.service

import com.example.jetpacktaskmanagement.entity.Task
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskService {
    @GET("api/task/{userId}")
    suspend fun getUserTasks(@Path("userId") userId: Int): List<Task>

    @GET("api/task")
    suspend fun getTask(@Query("id") id: Int): Task
}

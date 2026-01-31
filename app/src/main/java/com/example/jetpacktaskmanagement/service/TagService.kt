package com.example.jetpacktaskmanagement.service

import com.example.jetpacktaskmanagement.entity.Tag
import retrofit2.http.GET
import retrofit2.http.Query

interface TagService {
    @GET("api/tags/all")
    suspend fun getAllTags(): List<Tag>

    @GET("api/tags")
    suspend fun getTagByTaskId(@Query("taskId") taskId: Int): List<Tag>

}
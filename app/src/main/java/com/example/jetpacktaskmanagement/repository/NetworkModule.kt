package com.example.jetpacktaskmanagement.repository

import com.example.jetpacktaskmanagement.model.IUiState
import com.example.jetpacktaskmanagement.model.UIState
import com.example.jetpacktaskmanagement.model.UiStateViewModel
import com.example.jetpacktaskmanagement.service.TagService
import com.example.jetpacktaskmanagement.service.TaskService
import com.example.jetpacktaskmanagement.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideTaskService(retrofit: Retrofit): TaskService {
        return retrofit.create(TaskService::class.java)
    }

    @Provides
    @Singleton
    fun provideTagService(retrofit: Retrofit): TagService {
        return retrofit.create(TagService::class.java)
    }

    @Provides
    fun provideIUiState(): IUiState {
        return UiStateViewModel(UIState.Loading)
    }
}
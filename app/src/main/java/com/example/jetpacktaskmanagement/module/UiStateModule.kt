package com.example.jetpacktaskmanagement.module

import com.example.jetpacktaskmanagement.model.IUiState
import com.example.jetpacktaskmanagement.model.UIState
import com.example.jetpacktaskmanagement.model.UiStateViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UiStateModule {
    @Provides
    @Singleton
    fun provideIUiState(): IUiState {
        return UiStateViewModel(UIState.Loading)
    }
}

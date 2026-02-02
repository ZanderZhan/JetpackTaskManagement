package com.example.jetpacktaskmanagement.model

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

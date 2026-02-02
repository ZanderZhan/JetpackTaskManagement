package com.example.jetpacktaskmanagement.dao

import android.content.Context
import androidx.room.Room
import com.example.jetpacktaskmanagement.ThemeDataStore
import com.example.jetpacktaskmanagement.dao.AppRoom.Companion.MIGRATION_2_3
import com.example.jetpacktaskmanagement.dao.AppRoom.Companion.MIGRATION_3_4
import com.example.jetpacktaskmanagement.dao.AppRoom.Companion.MIGRATION_9_10
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppRoom {
        return Room.databaseBuilder(
            context.applicationContext,
            AppRoom::class.java,
            "task_database"
        ).addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_9_10)
            .build()

    }

    @Provides
    fun provideUserDao(database: AppRoom): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideTaskDao(database: AppRoom): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideTagDao(database: AppRoom): TagDao {
        return database.tagDao()
    }

    @Provides
    @Singleton
    fun provideThemeDataStore(@ApplicationContext context: Context): ThemeDataStore {
        return ThemeDataStore(context)
    }
}
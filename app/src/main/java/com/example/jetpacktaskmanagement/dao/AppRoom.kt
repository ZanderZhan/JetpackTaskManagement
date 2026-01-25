package com.example.jetpacktaskmanagement.dao

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.jetpacktaskmanagement.entity.Task
import com.example.jetpacktaskmanagement.entity.User

@Database(
    entities = [User::class, Task::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppRoom : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var _INSTANCE: AppRoom? = null

        fun getDatabase(context: Context): AppRoom {
            return _INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoom::class.java,
                    "task_database"
                )
                .build()
                _INSTANCE = instance
                instance
            }
        }
    }
}
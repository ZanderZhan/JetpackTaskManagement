package com.example.jetpacktaskmanagement.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.jetpacktaskmanagement.entity.Task
import com.example.jetpacktaskmanagement.entity.User
import com.example.jetpacktaskmanagement.repository.TaskListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Database(
    entities = [User::class, Task::class],
    version = 4,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppRoom : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var _INSTANCE: AppRoom? = null
        
        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun getDatabase(context: Context): AppRoom {
            return _INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppRoom::class.java, "task_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate database with initial users and tasks
                            _INSTANCE?.let { database ->
                                applicationScope.launch {
                                    populateDatabase(database.userDao(), database.taskDao())
                                }
                            }
                        }
                    })
                    .build()
                _INSTANCE = instance
                instance
            }
        }
        
        private suspend fun populateDatabase(userDao: UserDao, taskDao: TaskDao) {
            val repository = TaskListRepository()
            val users = repository.generateUsers()
            val tasks = repository.generateTasks()
            
            userDao.insert(users)
            taskDao.saveTasks(tasks)
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create a temporary table with the new schema
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `tasks_temp` (" + "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + "`checked` INTEGER NOT NULL, " + "`description` TEXT NOT NULL, " + "`date` INTEGER NOT NULL)"
                )

                // 2. Preserve old Date() strings by parsing them in code
                // We query the old table
                val cursor = db.query("SELECT id, checked, description, date FROM tasks")

                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)

                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getInt(0)
                        val checked = cursor.getInt(1)
                        val description = cursor.getString(2)
                        val dateString = cursor.getString(3)

                        val timestamp: Long = try {
                            sdf.parse(dateString)?.time ?: 0L
                        } catch (e: Exception) {
                            // Fallback if parsing fails
                            0L
                        }

                        val values = ContentValues().apply {
                            put("id", id)
                            put("checked", checked)
                            put("description", description)
                            put("date", timestamp)
                        }

                        db.insert("tasks_temp", SQLiteDatabase.CONFLICT_REPLACE, values)
                    } while (cursor.moveToNext())
                }
                cursor.close()

                // 3. Swap tables
                db.execSQL("DROP TABLE tasks")
                db.execSQL("ALTER TABLE tasks_temp RENAME TO tasks")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create a temporary table with the new schema
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `tasks_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `checked` INTEGER NOT NULL, `description` TEXT NOT NULL, `date` INTEGER NOT NULL)"
                )

                // 2. We query the old table
                val cursor = db.query("SELECT id, checked, description, date FROM tasks")


                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getInt(0)
                        val checked = cursor.getInt(1)
                        val description = cursor.getString(2)
                        val date = cursor.getLong(3)

                        val values = ContentValues().apply {
                            put("id", id)
                            put("userId", (0..9).random())
                            put("checked", checked)
                            put("description", description)
                            put("date", date)
                        }

                        db.insert("tasks_temp", SQLiteDatabase.CONFLICT_REPLACE, values)
                    } while (cursor.moveToNext())
                }
                cursor.close()

                // 3. Swap tables
                db.execSQL("DROP TABLE tasks")
                db.execSQL("ALTER TABLE tasks_temp RENAME TO tasks")
            }
        }
    }
}
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
import com.example.jetpacktaskmanagement.entity.Tag
import com.example.jetpacktaskmanagement.entity.Task
import com.example.jetpacktaskmanagement.entity.TaskWithTagCrossRef
import com.example.jetpacktaskmanagement.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.text.SimpleDateFormat
import java.util.Locale

@Database(
    entities = [User::class, Task::class, Tag::class, TaskWithTagCrossRef::class],
    version = 10,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
    ]
)
abstract class AppRoom : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun taskDao(): TaskDao

    abstract fun tagDao(): TagDao

    companion object {
        @Volatile
        private var _INSTANCE: AppRoom? = null

        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun getDatabase(context: Context): AppRoom {
            return _INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext, AppRoom::class.java, "task_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_9_10)
                    .build().also { _INSTANCE = it }
            }
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
                // 1. Create users if they don't already exist (for migrated databases)
                val userNames = listOf(
                    "Alice", "Bob", "Charlie", "David", "Eve",
                    "Frank", "Grace", "Heidi", "Ivan", "Judy"
                )
                val genders = listOf(0, 1, 2) // UNSPECIFIED, MALE, FEMALE

                // Check if users table exists and has any users
                val userCursor = db.query("SELECT COUNT(*) FROM users")
                var userCount = 0
                if (userCursor.moveToFirst()) {
                    userCount = userCursor.getInt(0)
                }
                userCursor.close()

                // If no users exist, create 10 default users
                if (userCount == 0) {
                    for (i in 0 until 10) {
                        val values = ContentValues().apply {
                            put("name", userNames[i])
                            put("gender", genders.random())
                        }
                        db.insert("users", SQLiteDatabase.CONFLICT_IGNORE, values)
                    }
                }

                // 2. Create a temporary table with the new schema including foreign key and index
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `tasks_temp` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `userId` INTEGER NOT NULL,
                        `checked` INTEGER NOT NULL,
                        `description` TEXT NOT NULL,
                        `date` INTEGER NOT NULL,
                        FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                // Create index on userId
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_temp_userId` ON `tasks_temp` (`userId`)")

                // 3. We query the old table
                val cursor = db.query("SELECT id, checked, description, date FROM tasks")


                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getInt(0)
                        val checked = cursor.getInt(1)
                        val description = cursor.getString(2)
                        val date = cursor.getLong(3)

                        val values = ContentValues().apply {
                            put("id", id)
                            // Assign to users with IDs 1-10 (auto-generated IDs start from 1)
                            put("userId", (1..10).random())
                            put("checked", checked)
                            put("description", description)
                            put("date", date)
                        }

                        db.insert("tasks_temp", SQLiteDatabase.CONFLICT_REPLACE, values)
                    } while (cursor.moveToNext())
                }
                cursor.close()

                // 4. Swap tables
                db.execSQL("DROP TABLE tasks")
                db.execSQL("ALTER TABLE tasks_temp RENAME TO tasks")
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Migration from version 9 to 10 handles:
                // Changing task_tag_cross_ref foreign key constraints from CASCADE to NO ACTION
                
                // Recreate task_tag_cross_ref table with NO ACTION foreign keys
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `task_tag_cross_ref_temp` (
                        `taskId` INTEGER NOT NULL,
                        `tagId` INTEGER NOT NULL,
                        PRIMARY KEY(`taskId`, `tagId`),
                        FOREIGN KEY(`taskId`) REFERENCES `tasks`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
                        FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
                    )
                    """.trimIndent()
                )
                
                // Create index on tagId
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_task_tag_cross_ref_temp_tagId` ON `task_tag_cross_ref_temp` (`tagId`)")
                
                // Copy data from old table
                db.execSQL("INSERT INTO task_tag_cross_ref_temp SELECT taskId, tagId FROM task_tag_cross_ref")
                
                // Drop old table and rename temp table
                db.execSQL("DROP TABLE task_tag_cross_ref")
                db.execSQL("ALTER TABLE task_tag_cross_ref_temp RENAME TO task_tag_cross_ref")
            }
        }
    }
}
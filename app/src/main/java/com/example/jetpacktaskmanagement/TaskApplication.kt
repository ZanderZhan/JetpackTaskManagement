package com.example.jetpacktaskmanagement

import android.app.Application
import com.example.jetpacktaskmanagement.dao.AppRoom
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskApplication: Application() {
    val database by lazy { AppRoom.getDatabase(this) }

}
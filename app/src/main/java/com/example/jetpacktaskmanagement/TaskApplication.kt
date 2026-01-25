package com.example.jetpacktaskmanagement

import android.app.Application
import com.example.jetpacktaskmanagement.dao.AppRoom

class TaskApplication: Application() {
    val database by lazy { AppRoom.getDatabase(this) }

}
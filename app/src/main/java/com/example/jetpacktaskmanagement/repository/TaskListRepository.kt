package com.example.jetpacktaskmanagement.repository

import com.example.jetpacktaskmanagement.entity.Task
import java.sql.Date

class TaskListRepository() {

    private fun getRandomDate(): String {
        val tenDaysInMs = 10L * 24 * 60 * 60 * 1000
        val randomOffset = (0..tenDaysInMs).random()
        return Date(System.currentTimeMillis() - randomOffset).toString()
    }


    fun getNetworkTasks(): List<Task> {
        return listOf(
            Task(0, false, "Buy groceries", getRandomDate()),
            Task(0, true, "Finish project proposal", getRandomDate()),
        )
    }

}
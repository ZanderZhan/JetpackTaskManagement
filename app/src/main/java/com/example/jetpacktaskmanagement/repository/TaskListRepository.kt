package com.example.jetpacktaskmanagement.repository

import com.example.jetpacktaskmanagement.model.Task
import java.util.Date

class TaskListRepository {

    private fun getRandomDate(): Date {
        val tenDaysInMs = 10L * 24 * 60 * 60 * 1000
        val randomOffset = (0..tenDaysInMs).random()
        return Date(System.currentTimeMillis() - randomOffset)
    }

    fun getLocalTasks(): List<Task> {
        return listOf(
            Task(false, "Buy groceries", getRandomDate()),
            Task(true, "Finish project proposal", getRandomDate()),
            Task(false, "Call mom", getRandomDate()),
            Task(false, "Go for a run", getRandomDate()),
            Task(true, "Read a book", getRandomDate()),
            Task(false, "Water the plants", getRandomDate()),
            Task(false, "Clean the kitchen", getRandomDate()),
            Task(true, "Pay bills", getRandomDate()),
            Task(false, "Schedule dentist appointment", getRandomDate()),
            Task(false, "Organize workspace", getRandomDate())
        )
    }

    fun getNetworkTasks(): List<Task> {
        return listOf(
            Task(false, "Buy groceries", getRandomDate()),
            Task(true, "Finish project proposal", getRandomDate()),
        )
    }

}
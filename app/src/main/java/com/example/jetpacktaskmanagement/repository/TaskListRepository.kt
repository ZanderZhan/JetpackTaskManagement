package com.example.jetpacktaskmanagement.repository

import com.example.jetpacktaskmanagement.entity.Gender
import com.example.jetpacktaskmanagement.entity.Task
import com.example.jetpacktaskmanagement.entity.User

class TaskListRepository() {

    private fun getRandomDate(): Long {
        val tenDaysInMs = 10L * 24 * 60 * 60 * 1000
        val randomOffset = (0..tenDaysInMs).random()
        return System.currentTimeMillis() - randomOffset
    }

    fun getNetworkTasks(): List<Task> {
        return listOf(
            Task(0, false, "Buy groceries", getRandomDate()),
            Task(0, true, "Finish project proposal", getRandomDate()),
        )
    }

    fun generateTasks(): List<Task> {
        val descriptions = listOf(
            "Buy groceries",
            "Finish project proposal",
            "Go for a run",
            "Read a book",
            "Call mom",
            "Clean the house",
            "Pay bills",
            "Work on side project",
            "Mediate",
            "Cook dinner",
            "Walk the dog",
            "Study for exam",
            "Water the plants",
            "Fix the sink",
            "Organize desk",
            "Write a blog post",
            "Plan weekend trip",
            "Learn a new recipe",
            "Watch a movie",
            "Check emails"
        )
        return List(20) { index ->
            Task(
                id = 0,
                checked = (0..1).random() == 1,
                description = descriptions.getOrElse(index) { "Task $index" },
                date = getRandomDate()
            )
        }
    }

    fun generateUsers(): List<User> {
        val names = listOf(
            "Alice", "Bob", "Charlie", "David", "Eve",
            "Frank", "Grace", "Heidi", "Ivan", "Judy"
        )
        return List(10) { index ->
            User(
                id = 0,
                name = names.getOrElse(index) { "User $index" },
                gender = Gender.entries.random()
            )
        }
    }

}

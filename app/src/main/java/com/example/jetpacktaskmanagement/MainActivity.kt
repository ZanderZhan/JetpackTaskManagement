package com.example.jetpacktaskmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.jetpacktaskmanagement.screen.TaskAdd
import com.example.jetpacktaskmanagement.screen.TaskAddScreen
import com.example.jetpacktaskmanagement.screen.TaskList
import com.example.jetpacktaskmanagement.screen.TaskListScreen
import com.example.jetpacktaskmanagement.ui.theme.JetpackTaskManagementTheme
import com.example.jetpacktaskmanagement.viewmodel.TaskListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackTaskManagementTheme {
                JetpackTaskManagementApp()
            }
        }
    }
}

@Composable
fun JetpackTaskManagementApp() {
    val backStack = remember { mutableStateListOf<Any>(TaskList) }
    val viewModel: TaskListViewModel = viewModel()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is TaskList -> NavEntry(key) {
                    TaskListScreen(
                        viewModel = viewModel,
                        onAddTask = { backStack.add(TaskAdd) }
                    )
                }
                is TaskAdd -> NavEntry(key) {
                    TaskAddScreen(
                        viewModel = viewModel,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                else -> NavEntry(Unit) { }
            }
        }
    )
}

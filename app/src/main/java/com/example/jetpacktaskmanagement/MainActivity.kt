package com.example.jetpacktaskmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.jetpacktaskmanagement.screen.TagKey
import com.example.jetpacktaskmanagement.screen.TagScreen
import com.example.jetpacktaskmanagement.screen.TaskAdd
import com.example.jetpacktaskmanagement.screen.TaskAddScreen
import com.example.jetpacktaskmanagement.screen.TaskDetail
import com.example.jetpacktaskmanagement.screen.TaskDetailScreen
import com.example.jetpacktaskmanagement.screen.TaskList
import com.example.jetpacktaskmanagement.screen.TaskListScreen
import com.example.jetpacktaskmanagement.ui.theme.JetpackTaskManagementTheme
import com.example.jetpacktaskmanagement.viewmodel.TagViewModel
import com.example.jetpacktaskmanagement.viewmodel.TaskDetailViewModel
import com.example.jetpacktaskmanagement.viewmodel.TaskListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val themeDataStore = ThemeDataStore(application)
        setContent {
            val darkTheme = themeDataStore.isDarkTheme
                .collectAsState(false)
            JetpackTaskManagementTheme(
                darkTheme = darkTheme.value
            ) {
                JetpackTaskManagementApp()
            }
        }
    }
}

@Composable
fun JetpackTaskManagementApp(
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val backStack = rememberSaveable { mutableStateListOf<Any>(TaskList) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is TaskList -> NavEntry(key) {
                    TaskListScreen(
                        viewModel = viewModel,
                        onAddTask = {
                            backStack.add(TaskAdd)
                        },
                        onDetail = { taskId ->
                            backStack.add(TaskDetail(taskId))
                        }
                    )
                }

                is TaskAdd -> NavEntry(key) {
                    TaskAddScreen(
                        viewModel = viewModel,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }

                is TaskDetail -> NavEntry(key) {
                    val viewModel: TaskDetailViewModel = hiltViewModel(
                        creationCallback = { factory: TaskDetailViewModel.Factory ->
                            factory.create(key)
                        }
                    )
                    TaskDetailScreen(viewModel, onBack = {
                        backStack.removeLastOrNull()
                    }, onTag = { tagId ->
                        backStack.add(TagKey(tagId))
                    })
                }

                is TagKey -> NavEntry(key) {
                    val viewModel: TagViewModel = hiltViewModel(
                        creationCallback = { factory: TagViewModel.Factory ->
                            factory.create(key)
                        }
                    )
                    TagScreen(viewModel, onBack = {
                        backStack.removeLastOrNull()
                    })
                }

                else -> NavEntry(Unit) { }
            }
        }
    )
}

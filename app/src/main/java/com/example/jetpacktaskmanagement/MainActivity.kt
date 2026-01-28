package com.example.jetpacktaskmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackTaskManagementTheme {
                val viewModelStoreOwner: ViewModelStoreOwner =
                    checkNotNull(LocalViewModelStoreOwner.current)
                val viewModel: TaskListViewModel = viewModel(
                    factory = TaskListViewModel.provideFactory(),
                    extras = MutableCreationExtras(
                        if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
                            viewModelStoreOwner.defaultViewModelCreationExtras
                        } else {
                            CreationExtras.Empty
                        }
                    ).apply {
                        this[TaskListViewModel.REPOSITORY_KEY] =
                            TaskListViewModel.taskListRepository
                    }
                )

                JetpackTaskManagementApp(viewModel)
            }
        }
    }
}

@Composable
fun JetpackTaskManagementApp(viewModel: TaskListViewModel) {
    // todo : rememberSaveable doesn't survive after system-initiated process death?
    // why? https://www.revenuecat.com/blog/engineering/remember-vs-remembersaveable/
    val backStack = rememberSaveable { mutableStateListOf<Any>(TaskList) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
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
                    val viewModel: TaskDetailViewModel = viewModel(
                        factory = TaskDetailViewModel.provideFactory(key.detailId),
                        key = key.detailId.toString(),
                    )
                    TaskDetailScreen(viewModel, onBack = {
                        backStack.removeLastOrNull()
                    }, onTag = { tagId ->
                        backStack.add(TagKey(tagId))
                    })
                }

                is TagKey -> NavEntry(key) {
                    val viewModel: TagViewModel = viewModel(
                        factory = TagViewModel.provideFactory(key.tagId),
                        key = key.tagId.toString(),
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

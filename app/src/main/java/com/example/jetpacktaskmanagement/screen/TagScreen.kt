package com.example.jetpacktaskmanagement.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.jetpacktaskmanagement.viewmodel.TagViewModel
import kotlinx.serialization.Serializable

@Serializable
data class TagKey(val tagId: Int) : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagScreen(
    viewModel: TagViewModel,
    onBack: () -> Unit = {}
) {
    val tagWithTasks by viewModel.tagWithTasks.observeAsState()
    val tag = tagWithTasks?.keys?.firstOrNull()
    val tasks = tagWithTasks?.values?.firstOrNull() ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = tag?.name ?: "Tag Details")
                        tag?.priority?.let {
                            Text(
                                text = "Priority: ${it.name}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onClick = { /* ViewModel doesn't support toggle here yet */ },
                    onToggle = { /* ViewModel doesn't support toggle here yet */ },
                    onDelete = { /* ViewModel doesn't support delete here yet */ }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            if (tasks.isEmpty()) {
                item {
                    Text(
                        text = "No tasks found for this tag.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

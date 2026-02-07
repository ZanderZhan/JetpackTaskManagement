package com.example.jetpacktaskmanagement.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.jetpacktaskmanagement.entity.Task
import retrofit2.HttpException
import java.io.IOException

class TaskPagingSource(private val repository: TaskRepository) : PagingSource<Int, Task>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Task> {
        return try {

//            suspend fun loadTasks(key: Int, loadSize: Int): List<Task> {
//                return repository.getTasksByTaskIdAndCount(key, loadSize)
//            }

//            val key = params.key ?: 0
//            var tasks = loadTasks(key, params.loadSize)

//            if (tasks.size < params.loadSize) {
//                val lastId = if (tasks.isNotEmpty()) tasks.last().id else key
//                repository.loadTasksFromNetwork(lastId, params.loadSize - tasks.size)
//                tasks = loadTasks(key, params.loadSize)
//            }

            // for for demonstrate, just load from network, don't fetch from local
            val key = params.key
            val tasks = repository.loadTasksFromNetwork(key ?: 0, params.loadSize)

            LoadResult.Page(
                data = tasks,
                prevKey = null,
                nextKey = if (tasks.isEmpty()) null else tasks.last().id + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Task>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

package com.example.jetpacktaskmanagement.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

interface IUiState {
    val uiState: LiveData<UIState>
    fun updateUiState(updated: UIState)

    fun <T> addSource(source: LiveData<T>, onChanged: (data: T) -> UIState)
}

class UiStateViewModel(private val initialUiState: UIState = UIState.Loading) : IUiState {
    private val _uiState = MediatorLiveData<UIState>(initialUiState)

    override val uiState: LiveData<UIState> = _uiState

    override fun updateUiState(updated: UIState) {
        _uiState.value = updated
    }

    override fun <T> addSource(source: LiveData<T>, onChanged: (data: T) -> UIState) {
        _uiState.addSource(source) { _uiState.value = onChanged(it) }
    }

}